/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include <android/log.h>
#include <stdio.h>
#include <string.h>
#include <functional>
#include <webrtc/common_audio/vad/include/webrtc_vad.h>
#include <webrtc/modules/audio_processing/aec/include/echo_cancellation.h>
#include <webrtc/modules/audio_processing/aecm/include/echo_control_mobile.h>
#include <webrtc/modules/audio_processing/ns/include/noise_suppression.h>
#include <webrtc/modules/audio_processing/ns/include/noise_suppression_x.h>
#include <webrtc/modules/audio_processing/aecm/include/echo_control_mobile.h>
#include <webrtc/modules/audio_processing/agc/include/gain_control.h>

using namespace std;

class onceToken {
public:
    typedef function<void(void)> task;
    onceToken(const task &onConstructed, const task &_onDestructed = nullptr) {
        if (onConstructed) {
            onConstructed();
        }
        onDestructed = _onDestructed;
    }
    virtual ~onceToken() {
        if (onDestructed) {
            onDestructed();
        }
    }
private:
    onceToken();
    onceToken(const onceToken &);
    onceToken(onceToken &&);
    onceToken &operator =(const onceToken &);
    onceToken &operator =(onceToken &&);
    task onDestructed;
};

#ifdef __cplusplus
extern "C" {
#endif

#define JNI_API(retType, funName, ...) extern "C"  JNIEXPORT retType Java_com_lois_tytool_webrtc_apm_WebRtcJni_##funName(JNIEnv* env, jclass cls,__VA_ARGS__)
#define JNI_API1(retType, funName) extern "C"  JNIEXPORT retType Java_com_lois_tytool_webrtc_apm_WebRtcJni_##funName(JNIEnv* env, jclass cls)

JNI_API1(jlong,WebRtcVad_1Create){
    VadInst *handle = nullptr;
    if(0 == WebRtcVad_Create(&handle)){
        return (jlong)handle;
    }
    return 0;
}

JNI_API(jint ,WebRtcVad_1Free,jlong ctx){
    VadInst *handle = (VadInst *)ctx;
    return WebRtcVad_Free(handle);
}

JNI_API(jint ,WebRtcVad_1Init,jlong ctx){
    VadInst *handle = (VadInst *)ctx;
    return WebRtcVad_Init(handle);
}

JNI_API(jint ,WebRtcVad_1set_1mode,jlong ctx,jint mode){
    VadInst *handle = (VadInst *)ctx;
    return WebRtcVad_set_mode(handle,mode);
}

JNI_API(jint ,WebRtcVad_1Process,jlong ctx,jint fs,jshortArray audio_frame){
    VadInst *handle = (VadInst *)ctx;
    int16_t *short_ptr = env->GetShortArrayElements(audio_frame, 0);
    onceToken toke(nullptr,[&](){
        env->ReleaseShortArrayElements(audio_frame,short_ptr,0);
    });
    auto ret = WebRtcVad_Process(handle,fs,short_ptr,env->GetArrayLength(audio_frame));
    return ret;
}


JNI_API1(jlong,WebRtcNs_1Create){
    NsxHandle *handle = WebRtcNsx_Create();
    if(handle){
        return (jlong)handle;
    }
    return 0;
}

JNI_API(jint ,WebRtcNs_1Free,jlong ctx){
    NsxHandle *handle = (NsxHandle *)ctx;
    WebRtcNsx_Free(handle);
    return 0;
}

JNI_API(jint ,WebRtcNs_1Init,jlong ctx,jint fs){
    NsxHandle *handle = (NsxHandle *)ctx;
    return WebRtcNsx_Init(handle,fs);
}

JNI_API(jint ,WebRtcNs_1set_1policy,jlong ctx,jint mode){
    NsxHandle *handle = (NsxHandle *)ctx;
    return WebRtcNsx_set_policy(handle,mode);
}

JNI_API(jshortArray ,WebRtcNs_1Process,jlong ctx,jshortArray in,jint sampleMS){
    NsxHandle *handle = (NsxHandle *)ctx;
    short *in_ptr = env->GetShortArrayElements(in, 0);
    onceToken toke(nullptr,[&](){
        env->ReleaseShortArrayElements(in,in_ptr,0);
    });

    auto in_len = env->GetArrayLength(in);
    short out_ptr[in_len];

    auto pkt_count = sampleMS / 10;
    for (int i = 0; i < pkt_count; ++i) {
        int offset = i * (in_len / pkt_count);
        short *ptr_in = in_ptr + offset;
        short *ptr_out = out_ptr + offset;
        WebRtcNsx_Process(handle,(const short *const *)&ptr_in,1,&ptr_out);
    }

    auto out = env->NewShortArray(in_len);
    env->SetShortArrayRegion(out,0,in_len,out_ptr);
    return out;
}



JNI_API1(jlong,WebRtcAecm_1Create){
    void *handle = WebRtcAecm_Create();
    if(handle){
        return (jlong)handle;
    }
    return 0;
}

JNI_API(jint ,WebRtcAecm_1Free,jlong ctx){
    void *handle = (void *)ctx;
    WebRtcAecm_Free(handle);
    return 0;
}

JNI_API(jint ,WebRtcAecm_1Init,jlong ctx,jint sampFreq){
    void *handle = (void *)ctx;
    return WebRtcAecm_Init(handle,sampFreq);
}

JNI_API(jint ,WebRtcAecm_1BufferFarend,jlong ctx,jshortArray farend,jint nrOfSamples){
    void *handle = (void *)ctx;
    int16_t *in_ptr = env->GetShortArrayElements(farend, 0);
    onceToken toke(nullptr,[&](){
        env->ReleaseShortArrayElements(farend,in_ptr,0);
    });
    return WebRtcAecm_BufferFarend(handle,in_ptr,nrOfSamples);
}

JNI_API(jint ,WebRtcAecm_1BufferFarendBytes,jlong ctx,jbyteArray farend,jint nrOfSamples){
    void *handle = (void *)ctx;
    int16_t *in_ptr = (int16_t *)(env->GetByteArrayElements(farend, 0));
    onceToken toke(nullptr,[&](){
        env->ReleaseByteArrayElements(farend,(jbyte *)in_ptr,0);
    });
    return WebRtcAecm_BufferFarend(handle,in_ptr,nrOfSamples);
}

JNI_API(jshortArray ,WebRtcAecm_1Process,jlong ctx,jshortArray nearendNoisy,jshortArray nearendClean,jint nrOfSamples, jint msInSndCardBuf){
    void *handle = (void *)ctx;
    int16_t *nearendNoisy_ptr = env->GetShortArrayElements(nearendNoisy, 0);
    int16_t *nearendClean_ptr = nearendClean ? env->GetShortArrayElements(nearendClean, 0) : nullptr;

    onceToken toke(nullptr,[&](){
        env->ReleaseShortArrayElements(nearendNoisy,nearendNoisy_ptr,0);
        if(nearendClean_ptr){
            env->ReleaseShortArrayElements(nearendClean,nearendClean_ptr,0);
        }
    });

    auto nearendNoisy_len = env->GetArrayLength(nearendNoisy);
    int16_t out_ptr[nearendNoisy_len];

    if(0 != WebRtcAecm_Process(handle,nearendNoisy_ptr,nearendClean_ptr,out_ptr,nrOfSamples,msInSndCardBuf)){
        return nullptr;
    }

    auto out = env->NewShortArray(nearendNoisy_len);
    env->SetShortArrayRegion(out,0,nearendNoisy_len,out_ptr);
    return out;
}

JNI_API(jint ,WebRtcAecm_1set_1config,jlong ctx,jint cngMode,jint echoMode){
    void *handle = (void *)ctx;
    AecmConfig config ;
    config.cngMode = cngMode;
    config.echoMode = echoMode;
    return WebRtcAecm_set_config(handle,config);
}



JNI_API(jint ,WebRtcAgc_1AddFarend,jlong ctx,jshortArray inFar,jint samples){
    void *handle = (void *)ctx;
    int16_t *inFar_ptr = env->GetShortArrayElements(inFar, 0);

    onceToken toke(nullptr,[&](){
        env->ReleaseShortArrayElements(inFar,inFar_ptr,0);
    });
    return WebRtcAgc_AddFarend(handle,inFar_ptr,samples);
}

JNI_API(jint ,WebRtcAgc_1AddMic,jlong ctx,jshortArray inMic,jint samples){
    void *handle = (void *)ctx;
    int16_t *inMic_ptr = env->GetShortArrayElements(inMic, 0);
    onceToken toke(nullptr,[&](){
        env->ReleaseShortArrayElements(inMic,inMic_ptr,0);
    });
    return WebRtcAgc_AddMic(handle,inMic_ptr,NULL,samples);
}


JNI_API(jint ,WebRtcAgc_1VirtualMic,jlong ctx,jshortArray inMic,jint samples,jint micLevelIn,jintArray micLevelOut){
    void *handle = (void *)ctx;
    int16_t *inMic_ptr = env->GetShortArrayElements(inMic, 0);
    jint *micLevelOut_ptr = env->GetIntArrayElements(micLevelOut, 0);
    onceToken toke(nullptr,[&](){
        env->ReleaseShortArrayElements(inMic,inMic_ptr,0);
        env->ReleaseIntArrayElements(micLevelOut,micLevelOut_ptr,0);
    });

    int32_t micLevelOutTmp;
    auto ret = WebRtcAgc_VirtualMic(handle,inMic_ptr,NULL,samples,micLevelIn,&micLevelOutTmp);
    *micLevelOut_ptr = micLevelOutTmp;
    return ret;
}

JNI_API(jint ,WebRtcAgc_1Process,
        jlong ctx,
        jshortArray inNear,
        jint samples,
        jshortArray out,
        jint inMicLevel,
        jintArray outMicLevel,
        jint echo,
        jintArray saturationWarning){

    void *handle = (void *)ctx;
    int16_t *inNear_ptr = env->GetShortArrayElements(inNear, 0);
    int16_t *out_ptr = env->GetShortArrayElements(out, 0);
    jint *outMicLevel_ptr = env->GetIntArrayElements(outMicLevel,0);
    jint *saturationWarning_ptr = env->GetIntArrayElements(saturationWarning,0);

    onceToken toke(nullptr,[&](){
        env->ReleaseShortArrayElements(inNear,inNear_ptr,0);
        env->ReleaseShortArrayElements(out,out_ptr,0);
        env->ReleaseIntArrayElements(outMicLevel,outMicLevel_ptr,0);
        env->ReleaseIntArrayElements(saturationWarning,saturationWarning_ptr,0);
    });

    int32_t outMicLevelTmp;
    uint8_t saturationWarningTmp;
    auto ret = WebRtcAgc_Process(handle,inNear_ptr,NULL,samples,out_ptr,NULL,inMicLevel,&outMicLevelTmp,echo,&saturationWarningTmp);

    *outMicLevel_ptr = outMicLevelTmp;
    *saturationWarning_ptr = saturationWarningTmp;
    return ret;
}

JNI_API(jint ,WebRtcAgc_1set_1config,jlong ctx,jint targetLevelDbfs,jint compressionGaindB,jint limiterEnable){
    void *handle = (void *)ctx;
    WebRtcAgc_config_t config = {(int16_t)targetLevelDbfs,(int16_t)compressionGaindB,(uint8_t)limiterEnable};
    return WebRtcAgc_set_config(handle,config);
}

JNI_API1(jlong ,WebRtcAgc_1Create){
    void *ret = nullptr;
    WebRtcAgc_Create(&ret);
    return (jlong)ret;
}

JNI_API(jint ,WebRtcAgc_1Free,jlong ctx){
    void *handle = (void *)ctx;
    return WebRtcAgc_Free(handle);
}

JNI_API(jint ,WebRtcAgc_1Init,
        jlong ctx,
        jint minLevel,
        jint maxLevel,
        jint agcMode,
        jint fs){
    void *handle = (void *)ctx;
    return WebRtcAgc_Init(handle,minLevel,maxLevel,agcMode,fs);
}


#ifdef __cplusplus
}
#endif
