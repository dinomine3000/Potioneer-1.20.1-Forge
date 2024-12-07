package net.dinomine.potioneer.util;

public class PotioneerMathHelper {

    public static class MatrixHelper{
        public float[][] multiply(float[][] mat1, float[][] mat2){
            int[] dim1 = getDim(mat1);
            int[] dim2 = getDim(mat2);
            if(dim1[1] != dim2[0]){
                throw new RuntimeException("Matrices have incompatible dimensions: "
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

        public float[][] getRotationMatrixY(float theta){
            float[][] res = new float[][]{
                    {(float) Math.cos(theta), 0, (float) Math.sin(theta), 0},
                    {0, 1, 0, 0},
                    {(float) (-1f * Math.sin(theta)), 0, (float) Math.cos(theta), 0},
                    {0, 0, 0, 1}
            };
            return res;
        }
    }
}
