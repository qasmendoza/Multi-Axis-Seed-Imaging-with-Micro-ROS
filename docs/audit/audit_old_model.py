#!/usr/bin/env python3
"""
Re-runs the HAMR audit of the earlier model (MicroROS_Schematic.sysml).

HAMR stops at the first layer of errors, so each stage applies the minimal fix
for the errors just reported (to a scratch copy -- the original file is never
modified) and runs the checker again to expose the next layer.

usage: audit_old_model.py <MicroROS_Schematic.sysml> <sysml lib dir> [<sysml lib dir> ...]
       (lib dirs: the aadl.library and hamr.aadl.library folders of the course project)
"""
import os, re, shutil, subprocess, sys, tempfile

src, libs = sys.argv[1], sys.argv[2:]
sireum = os.path.join(os.environ["SIREUM_HOME"], "bin", "sireum")
work = tempfile.mkdtemp(prefix="old_model_audit_")
model = os.path.join(work, "MicroROS_Schematic.sysml")
shutil.copy(src, model)
text = open(model).read()

def tipe(title):
    print(f"\n######## {title}")
    r = subprocess.run([sireum, "hamr", "sysml", "tipe", "--sourcepath", ":".join([work] + libs)],
                       capture_output=True, text=True)
    out = (r.stdout + r.stderr).strip().splitlines()
    errs = [l for l in out if l.strip().startswith("- ")]
    print(f"   {len(errs)} error line(s)" if errs else "")
    for l in out[:25]:
        print("   " + l.replace(work + "/", ""))
    if len(out) > 25:
        print(f"   ... ({len(out) - 25} more lines)")

def save():
    open(model, "w").write(text)

tipe("Stage 0: the model exactly as it is")

# 1. 'state' is a reserved word (SysML v2 state usages, GUMBO state section)
text = text.replace("attribute state          : PowerRailState;", "attribute railState      : PowerRailState;")
text = text.replace("(state == PowerRailState", "(railState == PowerRailState").replace("powerSampleIn.state", "powerSampleIn.railState")
save(); tipe("Stage 1: field 'state' renamed to 'railState'")

# 2. in-port to in-port connections L298N_*.coilDriveIn -> Motor_*.coilDriveIn
for ax in "zxy":
    text = re.sub(r"\n\s*connection m%s: PortConnection\n\s*connect L298N_%s\.coilDriveIn to Motor_%s\.coilDriveIn;" % (ax, ax.upper(), ax.upper()), "", text)
save(); tipe("Stage 2: the three in->in L298N->Motor connections removed")

# 3. untyped integer literals compared with Unsigned_32 fields
text = text.replace("cameraFrameOut.widthPx == 1920 &", "cameraFrameOut.widthPx == 1920 [u32] &")
text = text.replace("cameraFrameOut.heightPx == 1080);", "cameraFrameOut.heightPx == 1080 [u32]);")
save(); tipe("Stage 3: typed literals in MR_HLR_4")

# 4. GUMBO-annotated thread definitions instantiated three times
for name in ["L298N_X", "L298N_Y", "Motor_X", "Motor_Y"]:
    text = re.sub(r"\n\s*part %s: [A-Za-z0-9_]+;[^\n]*" % name, "", text)
    text = re.sub(r"\n\s*allocation pb\d+: Deployment_Properties::Actual_Processor_Binding\n\s*allocate %s to esp32;" % name, "", text)
for c, tgt in [("gx", "L298N_X"), ("gy", "L298N_Y")]:
    text = re.sub(r"\n\s*connection %s: PortConnection\n\s*connect StepperCoordinator\.drive[XY]_out to %s\.coilDriveIn;" % (c, tgt), "", text)
save(); tipe("Stage 4: only one L298N / motor instance kept")

# 5. GUMBO clauses that refer to component attributes
text = re.sub(r"\n\s*guarantee MR_HLR_12 .*?;\n", "\n", text, flags=re.S)
text = re.sub(r"\n\s*guarantee MR_HLR_13 .*?;\n", "\n", text, flags=re.S)
save(); tipe("Stage 5: MR_HLR_12 / MR_HLR_13 removed")

# 6. remaining untyped literals
for a, b in [("railVoltage_mV >= 11400 & railVoltage_mV <= 12600", "railVoltage_mV >= 11400 [u32] & railVoltage_mV <= 12600 [u32]"),
             ("implies railVoltage_mV <  11400)", "implies railVoltage_mV <  11400 [u32])"),
             ("implies railVoltage_mV >  12600)", "implies railVoltage_mV >  12600 [u32])"),
             ("stepCount <= 200000;", "stepCount <= 200000 [u32];"),
             ("inv frameIsHD:  widthPx == 1920 & heightPx == 1080;", "inv frameIsHD:  widthPx == 1920 [u32] & heightPx == 1080 [u32];")]:
    text = text.replace(a, b)
save(); tipe("Stage 6: typed literals everywhere")

print("\n######## Stage 7: code generation for the model's own target (--platform Microkit)")
r = subprocess.run([sireum, "hamr", "sysml", "codegen", "--platform", "Microkit",
                    "--sel4-output-dir", os.path.join(work, "out_microkit"),
                    "--workspace-root-dir", work,
                    "--sourcepath", ":".join([work] + libs),
                    "--system-name", "MicroROS::SeedImagingSystem_i", model], capture_output=True, text=True)
for l in (r.stdout + r.stderr).strip().splitlines()[-6:]:
    print("   " + l.replace(work + "/", ""))
print(f"\n(scratch copies in {work}; the original file was not modified)")
