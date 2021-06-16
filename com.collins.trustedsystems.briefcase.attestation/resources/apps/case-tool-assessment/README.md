# How to build

The build files assume you are using an older version of the CakeML compiler, specifically version 1282. I believe everyone on the CASE projectis using the same version, but if not, here are links to this version:
- https://cakeml.org/regression/artefacts/1282/cake-x64-32.tar.gz
- https://cakeml.org/regression/artefacts/1282/cake-x64-64.tar.gz

You will need CMake, version 3.10.2 or higher. You also need CCMake if you need to configure the CMake parameters.

Create a build directory from the top-level of the repo, and then invoke cmake:
```sh
mkdir build && cd build
cmake ..
```
If you need to configure the CMake build parameters, enter `ccmake .`.

Specifically, you may be interested in the parameters `CAKE`, which is the location of the CakeML compiler, and `UserAM_Good`, which toggle whether or not the UserAM reports good or bad evidence.

You may perform additional configuration by directly editing the values in `Config.sml`.

You can build the UserAM (AKA the groundstation or AM test harness) with `make user_am`, and you can build the HeliAM with `make heli_am`, or you can build both with `make tool_assessment`. After the build is completed, you will find the static libraries `libuser_am.a` and `libheli_am.a` in `build/apps/case-tool-assessment`, representing the UserAM and HeliAM components, respectively.

There are also alternative build targets `heli_am_nocrypto`, `user_am_nocrypto`, and `tool_assessment_nocrypto`, which produces `libheli_am_nocrypto.a` and `libuser_am_nocrypto.a`. These represent the same components, but with the crypto stubbed out. This may be useful for testing/integration, since the crypto library is very platform dependent.
