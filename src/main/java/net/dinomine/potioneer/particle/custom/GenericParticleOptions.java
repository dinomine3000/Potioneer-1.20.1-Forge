package net.dinomine.potioneer.particle.custom;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dinomine.potioneer.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector4f;

public class GenericParticleOptions implements ParticleOptions {
    public static final Codec<GenericParticleOptions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("r").forGetter(o -> o.color.x()),
            Codec.FLOAT.fieldOf("g").forGetter(o -> o.color.y()),
            Codec.FLOAT.fieldOf("b").forGetter(o -> o.color.z()),
            Codec.FLOAT.fieldOf("a").forGetter(o -> o.color.w()),
            Codec.FLOAT.fieldOf("scale").forGetter(GenericParticleOptions::getScale),
            Codec.FLOAT.fieldOf("friction").forGetter(GenericParticleOptions::getFriction),
            Codec.FLOAT.fieldOf("gravity").forGetter(GenericParticleOptions::getGravity),
            Codec.INT.fieldOf("lifetime").forGetter(GenericParticleOptions::getLifetime)
    ).apply(instance, (r, g, b, a, scale, friction, gravity, lifetime) ->
            new GenericParticleOptions(new Vector4f(r, g, b, a), scale, friction, gravity, lifetime)));

    @SuppressWarnings("deprecation")
    public static final Deserializer<GenericParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public GenericParticleOptions fromCommand(ParticleType<GenericParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = reader.readFloat();
            reader.expect(' ');
            float g = reader.readFloat();
            reader.expect(' ');
            float b = reader.readFloat();
            reader.expect(' ');
            float a = reader.readFloat();
            reader.expect(' ');
            float scale = reader.readFloat();
            return new GenericParticleOptions(new Vector4f(r, g, b, a), scale, 0.98F, 0.0F, 20);
        }

        @Override
        public GenericParticleOptions fromNetwork(ParticleType<GenericParticleOptions> type, FriendlyByteBuf buf) {
            Vector4f color = new Vector4f(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat());
            float scale = buf.readFloat();
            float friction = buf.readFloat();
            float gravity = buf.readFloat();
            int lifetime = buf.readInt();
            return new GenericParticleOptions(color, scale, friction, gravity, lifetime);
        }
    };

    private final Vector4f color; // R, G, B, A (0.0F to 1.0F)
    private final float scale;
    private final float friction;
    private final float gravity;
    private final int lifetime;

    public GenericParticleOptions(Vector4f color, float scale, float friction, float gravity, int lifetime) {
        this.color = color;
        this.scale = scale;
        this.friction = friction;
        this.gravity = gravity;
        this.lifetime = lifetime;
    }

    // Convenience Constructor with defaults
    public GenericParticleOptions(Vector4f color, float scale) {
        this(color, scale, 0.98F, 0.0F, 20); // Default: light air resistance, no gravity, 20 ticks
    }

    @Override
    public ParticleType<GenericParticleOptions> getType() {
        return ModParticles.GENERIC_PARTICLE.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buf) {
        buf.writeFloat(this.color.x());
        buf.writeFloat(this.color.y());
        buf.writeFloat(this.color.z());
        buf.writeFloat(this.color.w());
        buf.writeFloat(this.scale);
        buf.writeFloat(this.friction);
        buf.writeFloat(this.gravity);
        buf.writeInt(this.lifetime);
    }

    @Override
    public String writeToString() {
        return String.format("%s %.2f %.2f %.2f %.2f %.2f",
                ModParticles.GENERIC_PARTICLE.getId(), color.x(), color.y(), color.z(), color.w(), scale);
    }

    public Vector4f getColor() { return color; }
    public float getScale() { return scale; }
    public float getFriction() { return friction; }
    public float getGravity() { return gravity; }
    public int getLifetime() { return lifetime; }
}