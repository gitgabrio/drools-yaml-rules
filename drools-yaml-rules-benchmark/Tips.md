Tips about benchmarkings
========================


Running via maven
-----------------

`  mvn package `

`   java -jar target/drools-yaml-rules-benchmark.jar`



If the evaluated method is meant to be _hot_ (repeatedly called) at target runtime, do use **Throughput** or **AverageTime** Mode.
If the evaluated method is meant to be _single-shot_ (called only once) at target runtime, do use **SingleShotTime** Mode.


Profiling
---------

If the reported error is suspicious (e.g. >= 10% of the measured result) do check for garbage collection influence:

    run with "-prof gc" or "-prof gc  -gc true" to profile gc and to have a (more) predictable gc impact.

Explanations:

-prof <profiler>            Use profilers to collect additional benchmark data.
Some profilers are not available on all JVMs and/or
all OSes. Please see the list of available profilers
with -lprof.

-gc <boolean>               Should JMH force GC between iterations? Forcing
the GC may help to lower the noise in GC-heavy benchmarks,
at the expense of jeopardizing GC ergonomics decisions.
Use with care.

To profile with async-profiler, if most of the expected invoked methods are _small_, add the following jvmarga

    jvmargs : -XX:UnlockDiagnosticVMOptions -XX:DebugNonSafepoints

Profiling
=========

1. Install [async-profile](https://github.com/jvm-profiling-tools/async-profiler)
2. make `profile.sh` executable
3. set required kernel properties (linux) 

    `sysctl kernel.perf_event_paranoid=1`
    `sysctl kernel.kptr_restrict=0`
4. start application to profile
5. retrieve application pid (e.g. with `jps`)
6. start profiler; e.g.
   `/path/to/profiler.sh -d 60 -f result.html 64266`
    profile the application with pid `64266` for 60 seconds (`-d 60`) and output result to the `result.html` file (`-f result.html`)