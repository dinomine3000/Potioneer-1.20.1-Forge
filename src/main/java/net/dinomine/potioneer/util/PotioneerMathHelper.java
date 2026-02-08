package net.dinomine.potioneer.util;

import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class PotioneerMathHelper {

    public static boolean isInteger(String testString){
        if(testString.isEmpty()) return false;
        testString = testString.strip();
        try {
            Integer.parseInt(testString);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public static Vec3 getRandomOrthogonalConstantY(Vec3 reference, boolean side, double magnitude){
        int b1 = side ? 1 : -1;
        Vec3 dodgeDir = new Vec3(b1, 0, -1*b1*reference.x/reference.z).normalize().scale(magnitude);
        double test = reference.x*dodgeDir.x + reference.z*dodgeDir.z;
        return dodgeDir;
    }

    public static class ProbabilityHelper{

        /**
         * returns a truly random pathway sequenc eid
         * @param random
         * @return
         */
        public static int getRandomId(RandomSource random){
            return Pathways.getRandomPathwayId(random)*10 + getRandomSequenceLevel(random.nextDouble());
        }

        /**
         * function that takes a sequence level and returns the base chance of generating such a sequence level
         * follows this desmos graph: <a href="https://www.desmos.com/calculator/ff70da0c33">...</a>
         * @param sequenceLevel
         * @return
         */
        private static double sequenceLevelFunction(int sequenceLevel){
            return 0.00232250*Math.pow(sequenceLevel, 2.2) + 0.003;
        }

        /**
         * function that will check if the formula for the given sequence should be generated, based on a random number from 0 to 1
         * @param rndNumber - random number from 0 to 1. the bigger it is, the higher the sequence level (closer to 0)
         * @return the random sequence level
         */
        private static int getRandomSequenceLevel(double rndNumber){
//             if(sequenceLevel == 5) return 5;
//            double chance = sequenceLevelFunction(sequenceLevel);
//            if(rndNumber < chance) {
//                return sequenceLevel;
//            }
//            return getRandomSequenceLevel(sequenceLevel - 1, Math.max(rndNumber - chance, 0));
//        this was my attempt at making this more readeable, dont feel like doing. you can have this to see the chances to generate each sequence (from sequence level 9 to 1)
//            ArrayList<Float> probabilities = new ArrayList<>(List.of(0.3f, 0.2f, 0.17f, 0.13f, 0.1f, 0.05203f, 0.02904f, 0.01367f, 0.005322f));
            ArrayList<Float> probabilities = new ArrayList<>(List.of(0.3f, 0.2f, 0.25f, 0.15f, 0.1f));
        return 9 - pickRandom(probabilities, (float) rndNumber);
        }

        /**
         * function that returns the appropriate pathway sequence id for whatever item might be desired by the player, taking into account their sequence level.
         * @param playerPathSeqId
         * @param luckManager
         * @param random
         * @param aptitudePathwayId
         * @return
         */
        public static int getRandomPathwaySequenceId(int playerPathSeqId, PlayerLuckManager luckManager, RandomSource random, int aptitudePathwayId){
            double nextSequenceChance = playerPathSeqId%10 == 0 || playerPathSeqId < 0 ? 0 : sequenceLevelFunction((playerPathSeqId-1)%10);
            double samePathwayChance = PotioneerCommonConfig.SAME_PATHWAY_CHANCE.get();
            if(luckManager.passesLuckCheck((float) nextSequenceChance, 0, 0, random)){
                if(playerPathSeqId < 0){
                    if(PotioneerCommonConfig.DO_APTITUDE_PATHWAYS.get()) return 10*aptitudePathwayId + 9;
                    else return 10*Pathways.getRandomPathwayId(random) + 9;
                }
                return playerPathSeqId - 1;
            }
            if(playerPathSeqId >= 0 && luckManager.passesLuckCheck((float) samePathwayChance, 0, 0, random)){
                if(PotioneerCommonConfig.DO_APTITUDE_PATHWAYS.get()) return 10*aptitudePathwayId + getRandomSequenceLevel(luckManager.nextFloat(random));
                else return 10*(Math.floorDiv(playerPathSeqId, 10)) + getRandomSequenceLevel(luckManager.nextFloat(random));
            }
            int pathwayId = Pathways.getRandomPathwayId(random);
            int sequenceLevel = getRandomSequenceLevel(luckManager.nextFloat(random));
            return 10*pathwayId + sequenceLevel;
        }

        public static int getRandomPathwaySequenceId(LivingEntityBeyonderCapability cap, RandomSource random){
            return getRandomPathwaySequenceId(cap.getPathwaySequenceId(), cap.getLuckManager(), random, cap.getCharacteristicManager().getAptitudePathway());
        }

        public static float bayes(float likelihood, float prior, float evidence){
            return likelihood * prior / evidence;
        }

        public static int pickRandom(List<Float> probabilities, float chance) {
            double cumulative = 0.0;

            for (int i = 0; i < probabilities.size(); i++) {
                cumulative += probabilities.get(i);
                if (chance < cumulative) {
                    return i;
                }
            }

            System.out.println("Warning: Provided probabilities do not add up to 1. returning the last probability");
            // Fallback (shouldn't happen if probs sum to 1.0)
            return probabilities.size() - 1;
        }

    }

    public static class MatrixHelper{
        public static final float[][] transformMatrix90 = new float[][]{
                {0, 0, 1, 0},
                {0, 1, 0, 0},
                {-1, 0, 0, 1},
                {0, 0, 0, 1}
        };
        public static final float[][] transformMatrix270 = new float[][]{
                {0, 0, -1, 1},
                {0, 1, 0, 0},
                {1, 0, 0, 0},
                {0, 0, 0, 1}
        };
        public static final float[][] transformMatrix180 = new float[][]{
                {-1, 0, 0, 1},
                {0, 1, 0, 0},
                {0, 0, -1, 1},
                {0, 0, 0, 1}
        };
        public static final float[][] transformMatrix0 = new float[][]{
                {1, 0, 0, 0},
                {0, 1, 0, 0},
                {0, 0, 1, 0},
                {0, 0, 0, 1}
        };
        public float[][] multiply(float[][] mat1, float[][] mat2){
            int[] dim1 = getDim(mat1);
            int[] dim2 = getDim(mat2);
            if(mat1 == null || mat2 == null){
                return null;
            }
            if(dim1[1] != dim2[0]){
                throw new RuntimeException("Matrices have incompatible dimensions for multiplication: "
                        + dim1[0] + "x" + dim1[1] + " vs "
                        + dim2[0] + "x" + dim2[1]);
            }
            float[][] res = new float[dim1[0]][dim2[1]];
            for (int i = 0; i < dim1[0]; i++) {
                for (int j = 0; j < dim2[1]; j++) {
                    for (int k = 0; k < dim2[0]; k++) {
                        res[i][j] += mat1[i][k] * mat2[k][j];
                    }
                }
            }
            return res;
        }

        private int[] getDim(float[][] mat){
            int[] res = new int[2];
            res[0] = mat.length;
            res[1] = mat[0].length;
            return res;
        }

        public float[][] getRandMatrix(int dim1, int dim2){
            float[][] res = new float[dim1][dim2];
            for (int i = 0; i < dim1; i++) {
                for (int j = 0; j < dim2; j++) {
                    res[i][j] = (float) Math.random();
                }
            }
            return res;
        }

        public String getString(float[][] mat){
            String res = "";
            for (int i = 0; i < mat.length; i++) {
                res = res.concat("[ ");
                for (int j = 0; j < mat[0].length; j++) {
                    res = res.concat(String.valueOf(mat[i][j]).concat(" "));
                }
                res = res.concat("]\n");
            }
            return res;
        }

        public float[][] getTranslationMatrix(float x, float y, float z){
            float[][] res = new float[][]{
                    {1, 0, 0, x},
                    {0, 1, 0, y},
                    {0, 0, 1, z},
                    {0, 0, 0, 1}
            };
            return res;
        }

        public static float[][] getPositionMatrix(float x, float y, float z){
            float[][] res = new float[][]{
                    {x},
                    {y},
                    {z},
                    {1}
            };
            return res;
        }

        public float[][] subMatrices(float[][] mat1, float[][] mat2){
            int[] dim1 = getDim(mat1);
            int[] dim2 = getDim(mat2);
            if(dim1[0] != dim2[0] || dim1[1] != dim2[1]){
                throw new RuntimeException("Matrices have incompatible dimensions for subtraction: "
                        + dim1[0] + "x" + dim1[1] + " vs "
                        + dim2[0] + "x" + dim2[1]);
            } else {
                float[][] res = new float[dim1[0]][dim1[1]];
                for (int i = 0; i < dim1[0]; i++) {
                    for (int j = 0; j < dim1[1]; j++) {
                        res[i][j] = mat2[i][j] - mat1[i][j];
                    }
                }
                return res;
            }

        }

        public float getVectorMag(float[][] vector){
            float sum = 0;
            for (int i = 0; i < vector.length; i++) {
                sum += vector[i][0] * vector[i][0];
            }
            return (float) Math.sqrt(sum);
        }

        public float[][] getMatrixTranspose(float[][] mat){
            int[] dim = getDim(mat);
            float[][] res = new float[dim[1]][dim[0]];
            for (int i = 0; i < dim[0]; i++) {
                for (int j = 0; j < dim[1]; j++) {
                    res[j][i] = mat[i][j];
                }
            }
            return res;
        }

        public float getAngleFromVector(float[][] vecMat1, float[][] vecMat2){
            float[][] dotProduct = multiply(getMatrixTranspose(vecMat1), vecMat2);
            float newMag = dotProduct[0][0] / (getVectorMag(vecMat1) * getVectorMag(vecMat2));
            System.out.println(getString(dotProduct));
            float result = (float) Math.acos(Mth.clamp(newMag, -1, 1));
            return result;
        }

        public float[][] getPositionMatrix(Vec3 vector){
            if(vector == null) return null;
            float[][] res = new float[][]{
                    {(float) vector.x()},
                    {(float) vector.x()},
                    {(float) vector.x()},
                    {1}
            };
            return res;

        }

        public float[][] getRotationMatrixY(float theta){
            float[][] res = new float[][]{
                    {(float) Math.cos(theta), 0, (float) Math.sin(theta), 0},
                    {0, 1, 0, 0},
                    {(float) (-1f * Math.sin(theta)), 0, (float) Math.cos(theta), 0},
                    {0, 0, 0, 1}
            };
            return res;
        }

        public float[][] getScaleMatrix(float scale){
            float[][] res = new float[][]{
                    {scale, 0, 0, 0},
                    {0, scale, 0, 0},
                    {0, 0, scale, 0},
                    {0, 0, 0, 1}
            };
            return res;
        }
    }
}
