package com.zcking.furyengine.engine.graph;

import com.zcking.furyengine.rendering.Mesh;
import com.zcking.furyengine.utils.ResourceUtils;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/*
When using this loader, please make sure to export the OBJ with the following:
    + Include Normals
    + Include Edges
    + Include UVs
    + Triangulate Faces
    + Objects as OBJ Objects

Also split edges when exporting as this loader cannot assign multiple texture
coordinates to the same vertex.

Note: documented for Blender.
 */


public class OBJLoader {

    public static Mesh loadMesh(String filePath) throws Exception {
        List<String> lines = ResourceUtils.readLines(filePath);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Face> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    // Geometric vertex
                    Vector3f vert = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(vert);
                    break;

                case "vt":
                    // Texture coordinate
                    Vector2f coord = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(coord);
                    break;

                case "vn":
                    //  Vertex normal
                    Vector3f norm = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(norm);
                    break;

                case "f":
                    // Face
                    Face face = new Face(tokens[1], tokens[2], tokens[3]);
                    faces.add(face);
                    break;

                default:
                    // Ignore other entries in the OBJ file
                    // TODO - Research and use other entries for more advanced usage?
                    break;
            }
        }

        return reorderLists(vertices, textures, normals, faces);
    }

    private static Mesh reorderLists(List<Vector3f> posList, List<Vector2f> textCoordList,
                                     List<Vector3f> normList, List<Face> facesList) {

        List<Integer> indices = new ArrayList<>();

        // Create position array in the order it has been declared
        float[] posArr = new float[posList.size() * 3];
        int i = 0;
        for (Vector3f pos : posList) {
            posArr[i * 3] = pos.x;
            posArr[i * 3 + 1] = pos.y;
            posArr[i * 3 + 2] = pos.z;
            i++;
        }
        float[] textCoordArr = new float[posList.size() * 2];
        float[] normArr = new float[posList.size() * 3];

        for (Face face : facesList) {
            IdxGroup[] faceVertexIndices = face.getFaceVertexIndices();
            for (IdxGroup indValue : faceVertexIndices) {
                processFaceVertex(indValue, textCoordList, normList,
                        indices, textCoordArr, normArr);
            }
        }
        int[] indicesArr = new int[indices.size()];
        indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();
        return new Mesh(posArr, textCoordArr, normArr, indicesArr);
    }

    private static void processFaceVertex(IdxGroup indices, List<Vector2f> textCoordList,
                                          List<Vector3f> normList, List<Integer> indicesList,
                                          float[] texCoordArr, float[] normArr) {

        // Set index for vertex coordinates
        int posIndex = indices.idxPos;
        indicesList.add(posIndex);

        // Reorder texture coordinates
        if (indices.idxTextCoord >= 0) {
            Vector2f textCoord = textCoordList.get(indices.idxTextCoord);
            texCoordArr[posIndex * 2] = textCoord.x;
            texCoordArr[posIndex * 2 + 1] = 1 - textCoord.y;
        }
        if (indices.idxVecNormal >= 0) {
            // Reorder vector normals
            Vector3f vecNorm = normList.get(indices.idxVecNormal);
            normArr[posIndex * 3] = vecNorm.x;
            normArr[posIndex * 3 + 1] = vecNorm.y;
            normArr[posIndex * 3 + 2] = vecNorm.z;
        }
    }

    protected static class IdxGroup {

        public static final int NO_VALUE = -1;

        public int idxPos;
        public int idxTextCoord;
        public int idxVecNormal;

        public IdxGroup() {
            idxPos = NO_VALUE;
            idxTextCoord = NO_VALUE;
            idxVecNormal = NO_VALUE;
        }

    }

    protected static class Face {

        private IdxGroup[] idxGroups;

        public Face(String v1, String v2, String v3) {
            idxGroups = new IdxGroup[3];

            // Parse each group
            idxGroups[0] = parseLine(v1);
            idxGroups[1] = parseLine(v2);
            idxGroups[2] = parseLine(v3);
        }

        private IdxGroup parseLine(String line) {
            IdxGroup idxGroup = new IdxGroup();

            String[] lineTokens = line.split("/");
            int length = lineTokens.length;
            idxGroup.idxPos = Integer.parseInt(lineTokens[0]) - 1; // OBJ indexes at 0

            if (length > 1) {
                // Can be empty if the OBJ doesn't have any texture coordinates
                String textCoords = lineTokens[1];
                idxGroup.idxTextCoord = textCoords.length() > 0 ? Integer.parseInt(textCoords) - 1 : IdxGroup.NO_VALUE;

                if (length > 2) {
                    idxGroup.idxVecNormal = Integer.parseInt(lineTokens[2]) - 1;
                }
            }

            return idxGroup;
        }

        public IdxGroup[] getFaceVertexIndices() {
            return idxGroups;
        }
    }

}
