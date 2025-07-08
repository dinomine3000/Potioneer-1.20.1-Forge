package net.dinomine.potioneer.util;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PotioneerMathHelper {

    public static class ProbabilityHelper{
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

        public float[][] getPositionMatrix(float x, float y, float z){
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
