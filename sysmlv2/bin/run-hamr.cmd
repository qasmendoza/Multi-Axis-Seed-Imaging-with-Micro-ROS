::/*#! 2> /dev/null                                 #
@ 2>/dev/null # 2>nul & echo off & goto BOF         #
if [ -z ${SIREUM_HOME} ]; then                      #
  echo "Please set SIREUM_HOME env var"             #
  exit -1                                           #
fi                                                  #
exec ${SIREUM_HOME}/bin/sireum slang run "$0" "$@"  #
:BOF
setlocal
if not defined SIREUM_HOME (
  echo Please set SIREUM_HOME env var
  exit /B -1
)
%SIREUM_HOME%\\bin\\sireum.bat slang run "%0" %*
exit /B %errorlevel%
::!#*/
// #Sireum

import org.sireum._

// Re-runs HAMR code generation for the Seed Imaging model from the command line.
// It does what the CodeIVE command "HAMR SysML CodeGen" does; the other options
// come from the //@ HAMR: lines at the top of SeedImaging.sysml.
//
//   sysmlv2/bin/run-hamr.cmd         both platforms: ros2, then JVM
//   sysmlv2/bin/run-hamr.cmd ros2    ROS 2 node + micro-ROS node      -> hamr/ros2
//   sysmlv2/bin/run-hamr.cmd JVM     Slang reference, Logika, GUMBOX  -> hamr/slang

val sysmlDir: Os.Path = Os.slashDir.up

val sireumBin: Os.Path = Os.path(Os.env("SIREUM_HOME").get) / "bin"
val sireum: Os.Path = sireumBin / (if (Os.isWin) "sireum.bat" else "sireum")

assert((sysmlDir / "aadl-lib").exists, s"${sysmlDir / "aadl-lib"} not present")

val platforms: ISZ[String] = if (Os.cliArgs.isEmpty) ISZ[String]("ros2", "JVM") else Os.cliArgs

for (platform <- platforms) {
  // JVM: no IVE project files (generate them on demand with: sireum proyek ive hamr/slang)
  val extra: ISZ[String] = if (platform == "JVM") ISZ[String]("--no-proyek-ive") else ISZ[String]()
  val codegenArgs: ISZ[String] =
    ISZ[String](sireum.value, "hamr", "sysml", "codegen", "--platform", platform) ++ extra ++
      ISZ[String]("--sourcepath", ".:aadl-lib", "--system-name", "SeedImaging::SeedImagingSystem", "SeedImaging.sysml")

  val results = Os.proc(codegenArgs).at(sysmlDir).echo.console.run()

  // Windows can report 23 (a platform restart request) after a successful run
  if (!(results.exitCode == 0 || results.exitCode == 23)) {
    println(results.err)
    Os.exit(results.exitCode)
  }
}

Os.exit(0)
