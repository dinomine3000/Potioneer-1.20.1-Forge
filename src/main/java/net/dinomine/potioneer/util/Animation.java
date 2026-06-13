package net.dinomine.potioneer.util;

import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.function.Function;

public class Animation {
    private final HashMap<String, Float> start = new HashMap<>();
    private final HashMap<String, Float> end = new HashMap<>();
    private final HashMap<String, Function<Float, Float>> converter = new HashMap<>();

    public Animation animateValue(String valueKey, float startValue, float endValue){
        start.put(valueKey, startValue);
        end.put(valueKey, endValue);
        return this;
    }

    public Animation animateValue(String valueKey, Function<Float, Float> converter){
        this.converter.put(valueKey, converter);
        return this;
    }

    protected float getValue(String valueKey, float percent){
        if(start.containsKey(valueKey))
            return Mth.lerp(percent, start.get(valueKey), end.get(valueKey));
        if(converter.containsKey(valueKey))
            return converter.get(valueKey).apply(percent);
        return 1;
    }

    protected float getValue(float start, float end, float percent){
        return Mth.lerp(percent, start, end);
    }

    protected float getValue(Function<Float, Float> converter, float percent){
        return converter.apply(percent);
    }
}
