cmake_minimum_required(VERSION 3.10.2)

set(UserAM_Good ON CACHE BOOL "Toggles whether the UserAM will respond with good or bad evidence.")
if(${UserAM_Good})
    get_files(user_golden GoldenHashes.sml)
else()
    get_files(user_golden BadGoldenHashes.sml)
endif()

# Heli build
get_files(heli_src_nocrypto Config.sml ${cop_src} HamrStandard.sml GoldenHashes.sml FakeCrypto.sml Heli.sml)
get_files(heli_c_src_nocrypto fake_crypto_ffi.c)
build_cake("heli_am_nocrypto"
    ENTRY_NAME "run"
    SOURCES "${heli_src_nocrypto}"
    C_SOURCES "${heli_c_src_nocrypto}"
)

get_files(heli_src Config.sml ${cop_src} ${crypto_src} HamrStandard.sml GoldenHashes.sml RealCrypto.sml Heli.sml)
build_cake("heli_am"
    ENTRY_NAME "run"
    SOURCES "${heli_src}"
)
add_hacl("heli_am")

get_files(fake_heli_c_src ../../system/basis_ffi.c fake_hamr_standard.c fake_heli_hamr.c)
build_cake("fake_heli_am.cake" SOURCES "${heli_src}")
add_executable(fake_heli_am ${fake_heli_c_src})
target_link_libraries(fake_heli_am fake_heli_am.cake)
add_hacl("fake_heli_am")

# User build
get_files(user_src_nocrypto Config.sml ${cop_src} HamrStandard.sml ${user_golden} FakeCrypto.sml User.sml)
get_files(user_c_src_nocrypto fake_crypto_ffi.c)
build_cake("user_am_nocrypto"
    ENTRY_NAME "run"
    SOURCES "${user_src_nocrypto}"
    C_SOURCES "${user_c_src_nocrypto}"
)

get_files(user_src Config.sml ${cop_src} ${crypto_src} HamrStandard.sml ${user_golden} RealCrypto.sml User.sml)
build_cake("user_am"
    ENTRY_NAME "run"
    SOURCES "${user_src}"
)
add_hacl("user_am")

get_files(fake_user_c_src ../../system/basis_ffi.c fake_hamr_standard.c fake_user_hamr.c)
build_cake("fake_user_am.cake" SOURCES "${user_src}")
add_executable("fake_user_am" ${fake_user_c_src})
target_link_libraries(fake_user_am fake_user_am.cake)
add_hacl("fake_user_am")

add_custom_target(tool_assessment_nocrypto DEPENDS heli_am_nocrypto user_am_nocrypto)
add_custom_target(tool_assessment DEPENDS heli_am user_am)
add_custom_target(fake_tool_assessment DEPENDS fake_heli_am fake_user_am)