# GUMBO Integration Constraints

The model carries one integration constraint pair. It sits on the
[`xPos`](../sysmlv2/SeedImaging.sysml#L116) connection and is the only place where a
property is stated by one component and depended on by the other.

---

## The pair

**Producer side**: [`SI-MCU-16`](../sysmlv2/SeedImaging.sysml#L224), in the
`integration` section of [`StepperController`](../sysmlv2/SeedImaging.sysml#L181):

```
guarantee SI_MCU_16_tiltAckInRange
  "SI-MCU-16: Every published tilt position lies within +/-45 deg.":
  SeedImaging::GUMBO__Library::isTiltSafe(xPos.data);
```

**Consumer side**: [`SI-HOST-A1`](../sysmlv2/SeedImaging.sysml#L358), in the
`integration` section of [`ScanController`](../sysmlv2/SeedImaging.sysml#L317):

```
assume SI_HOST_A1_tiltAckInRange
  "Tilt acknowledgements lie within +/-45 deg.":
  SeedImaging::GUMBO__Library::isTiltSafe(xPos.data);
```

Both reference the same predicate,
[`isTiltSafe`](../sysmlv2/SeedImaging.sysml#L78), which is defined once in the GUMBO
library against `TILT_LIMIT_STEPS`.

---

## What it establishes

The host maintains `tiltEstimate`, its belief about where the tilt axis is. It uses that
belief in [`SI-HOST-4`](../sysmlv2/SeedImaging.sysml#L392) to decide whether the next tilt
command is safe, and [`SI-HOST-10`](../sysmlv2/SeedImaging.sysml#L412) states the estimate
is always within ±45°.

But `tiltEstimate` is only ever updated from what arrives on `xPos`. `SI-HOST-10` is
therefore not provable from the host in isolation; it depends on what the other end of
the connection sends. `SI-HOST-A1` is the host naming that dependency, and `SI-MCU-16` is
the MCU discharging it.

Step 2 of `bin/verify.sh` runs SysMLv2 Logika over the connection and reports:

```
Checking integration constraints of SeedImagingSystem_Instance.xPos
Integration constraints verified!
```

---

## Why it matters here

Neither component is wrong in isolation if this pair is broken. Both still pass their own
unit tests, and both still satisfy their own compute contracts. The fault exists only in
the relationship between what one promises and what the other relies on.

That is demonstrated rather than asserted. `bin/seeded_bug_demo.sh model` weakens the
host's assumption to ±20 steps while leaving the MCU guaranteeing ±25:

```
== HAMR SysMLv2 Logika checking of the connections, expected to FAIL ==
Checking integration constraints of SeedImagingSystem_Instance.xPos
  - [116, 8] Could not deduce that the integration constraints of
    SeedImagingSystem_Instance.xPos holds

RESULT: the seeded integration-constraint error was caught by
        HAMR SysMLv2 Logika checking.
```

The report points at line 116, the `xPos` connection itself. The fault is caught at the
model level, before any code is generated.

---

## Why the other five connections carry none

`zPos` and `yPos` carry rotary positions. The host's plan sequencing does not depend on
their values, only on the fact that an acknowledgement arrived on the expected port, which
is covered by [`SI-HOST-14`](../sysmlv2/SeedImaging.sysml#L445) and
[`SI-HOST-18`](../sysmlv2/SeedImaging.sysml#L481).

The three command connections flow host to MCU. The MCU does not assume anything about
command values: [`SI-MCU-5`](../sysmlv2/SeedImaging.sysml#L259),
[`SI-MCU-9`](../sysmlv2/SeedImaging.sysml#L277) and
[`SI-MCU-13`](../sysmlv2/SeedImaging.sysml#L295) handle out-of-range values by refusing
them. That is a deliberate choice: the MCU is the component with physical authority, and
it was specified so that nothing the host can send will move an axis out of range,
whatever state the host is in.

The result is one integration constraint rather than several, and it sits exactly where
the two components' beliefs about the physical world have to agree.
