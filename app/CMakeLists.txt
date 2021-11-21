cmake_minimum_required(VERSION 3.4.1)
project(native_lib)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_INCLUDE_PATH  ${CMAKE_SOURCE_DIR}/src/main/jni/includes)
include_directories(${CMAKE_INCLUDE_PATH})

add_library(native_lib
        SHARED
        src/main/jni/ffmpeg_utils.cpp
        src/main/jni/mylog.c
        )
set(libs "${CMAKE_SOURCE_DIR}/libs/arm64-v8a")

#[[add_library(ffmpeg SHARED IMPORTED
        NAMES avcodec avdevice avfilter avformat avutil swresample swscale
        PATHS libs/armeabi-v8a)]]

function(buildlib)
    add_library(avcodec SHARED IMPORTED)
    #  设置目标导入路径，这里是一行
    set_target_properties(avcodec PROPERTIES IMPORTED_LOCATION ${libs}/libavcodec.so)

    add_library(avdevice SHARED IMPORTED)
    #  设置目标导入路径，这里是一行
    set_target_properties(avdevice PROPERTIES IMPORTED_LOCATION ${libs}/libavdevice.so)

    add_library(avfilter SHARED IMPORTED)
    #  设置目标导入路径，这里是一行
    set_target_properties(avfilter PROPERTIES IMPORTED_LOCATION ${libs}/libavfilter.so)

    add_library(avformat SHARED IMPORTED)
    #  设置目标导入路径，这里是一行
    set_target_properties(avformat PROPERTIES IMPORTED_LOCATION ${libs}/libavformat.so)

    add_library(avutil SHARED IMPORTED)
    #  设置目标导入路径，这里是一行
    set_target_properties(avutil PROPERTIES IMPORTED_LOCATION ${libs}/libavutil.so)

    add_library(swresample SHARED IMPORTED)
    #  设置目标导入路径，这里是一行
    set_target_properties(swresample PROPERTIES IMPORTED_LOCATION ${libs}/libswresample.so)


    add_library(swscale SHARED IMPORTED)
    #  设置目标导入路径，这里是一行


    set_target_properties(swscale PROPERTIES IMPORTED_LOCATION ${libs}/libswscale.so)
endfunction()


buildlib()
set(ffmpeg avcodec avdevice avfilter avformat avutil swresample swscale)

target_link_libraries(native_lib ${ffmpeg} log)

