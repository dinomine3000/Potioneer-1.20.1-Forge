package net.dinomine.potioneer.util;

import net.minecraft.util.Mth;
import java.util.HashMap;

public class AnimationHandler {
    private float speedMult = 1f;
    private long timestamp = System.currentTimeMillis();
    private float endTime = 1f;
    private float currentTime = 0f;
    private boolean doLerp = false;
    private boolean tickInReverse = false;
    private String animationPlaying = "";

    private HashMap<String, Animation> animations = new HashMap<>();

    public AnimationHandler(float endTime, boolean lerp){
        if(endTime == 0) throw new IllegalArgumentException("[Potioneer] Animation end time can not be 0");
        this.doLerp = lerp;
        this.endTime = endTime;
    }

    public boolean isPlaying(){
//        return (tickInReverse && currentTime > 0) || (!tickInReverse && currentTime < endTime);
        return currentTime > 0;
    }

    public AnimationHandler startAnimation(String animationKey, boolean playInReverse){
        if(playInReverse) this.currentTime = endTime;
        else this.currentTime = 0;
        return resumeAnimation(animationKey, playInReverse);
    }

    public AnimationHandler resumeAnimation(String animationKey, boolean playInReverse){
        this.tickInReverse = playInReverse;
        animationPlaying = animationKey;
        return this;
    }

    /*public String getCurrentAnimation(){return animationPlaying;}


    public void setSpeed(float newSpeed){
        speedMult = newSpeed;
    }

    public AnimationHandler invertTime(){return tickInReverse(!tickInReverse);}*/
    public AnimationHandler tickInReverse(boolean runInReverse){
        this.tickInReverse = runInReverse;
        return this;
    }

    public float tick(float deltaTime){
        if(currentTime > endTime && !tickInReverse) return 1f;
        if(currentTime < 0 && tickInReverse) return 0f;
        float dt = deltaTime*speedMult;
        if(doLerp){
            currentTime = Mth.lerp(dt, currentTime, tickInReverse ? 0 : endTime);
        } else {
            currentTime += dt * (tickInReverse ? -1 : 1);
        }
        return getProgress();
    }

    public AnimationHandler registerAnimation(String key, Animation animation){
        animations.put(key, animation);
        return this;
    }

    public Animation getAnimation(String animationKey){return animations.get(animationKey);}

    public float getValue(String animationKey, String valueKey){
        if(animations.containsKey(animationKey)) return animations.get(animationKey).getValue(valueKey, getProgress());
        return 0;
    }

    public float getValue(float start, float end, float percent){
        return Mth.lerp(percent, start, end);
    }

    public float getValue(float start, float end){
        return Mth.lerp(getProgress(), start, end);
    }

    public float tick(){
        return tick(getDeltaTime());
    }

    private float getProgress(){return Mth.clamp(currentTime/endTime, 0, 1);}

    private float getDeltaTime(){
        long curTime = System.currentTimeMillis();
        float deltaTime = (curTime - timestamp)/1000f;
        timestamp = curTime;
        return deltaTime;
    }
}
