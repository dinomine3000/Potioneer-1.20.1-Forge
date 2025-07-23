package net.dinomine.potioneer.particle.custom;


import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.dinomine.potioneer.particle.ModParticles;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;

public class IncenseSmokeParticleOptions implements ParticleOptions {
    public static final Deserializer<IncenseSmokeParticleOptions> DESERIALIZER = new Deserializer<>() {
        @Override
        public IncenseSmokeParticleOptions fromCommand(ParticleType<IncenseSmokeParticleOptions> type, StringReader reader) throws CommandSyntaxException {
            int color = reader.readInt();
            return new IncenseSmokeParticleOptions(color);
        }

        @Override
        public IncenseSmokeParticleOptions fromNetwork(ParticleType<IncenseSmokeParticleOptions> type, FriendlyByteBuf buffer) {
            return new IncenseSmokeParticleOptions(buffer.readInt());
        }
    };

    public static final Codec<IncenseSmokeParticleOptions> CODEC =
            Codec.INT.xmap(IncenseSmokeParticleOptions::new, p -> p.color);

    private final int color;

    public IncenseSmokeParticleOptions(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.INCENSE_PARTICLES.get();
    }

    @Override
    public void writeToNetwork(FriendlyByteBuf buffer) {
        buffer.writeInt(color);
    }

    @Override
    public String writeToString() {
        return Integer.toString(color);
    }
}
