package demos.labrynth;

import com.jogamp.opengl.GL4;
import engine.core.OpenGlObject;
import engine.feature.texture.TextureLoader;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class LabyrinthLevelCreator {
    ArrayList<OpenGlObject> createLevelFromFile(GL4 gl, String filePath) {
        int defaultSize = 25, vertSize, horSize;
        List<String> lines = new ArrayList<>();
        ArrayList<OpenGlObject> outList = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            lines = stream.filter(line -> line.matches("\\[[0-9]+,[0-9]+,[HV],[0-9]+\\];")).
                    collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (String l : lines) {
            l = l.replaceAll("[\\[,\\];]", " ");

            var scanner = new Scanner(l);
            int startX = scanner.nextInt();
            int startY = scanner.nextInt();
            String orientation = scanner.next("[HV]");
            int size = scanner.nextInt();

            if (orientation.equals("H")) {
                vertSize = defaultSize;
                horSize = size;
            } else {
                vertSize = size;
                horSize = defaultSize;
            }

            outList.add(createNewRectObject(startX, startY, horSize, vertSize, gl));
        }

        return outList;
    }

    ArrayList<OpenGlObject> createSlopesFromFile(GL4 gl, String filePath) {
        List<String> lines = new ArrayList<>();
        ArrayList<OpenGlObject> outList = new ArrayList<>();

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            lines = stream.filter(line -> line.matches("\\[[0-9]+,[0-9]+,[0-9]+,[0-9]+\\];")).
                    collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        for (String l : lines) {
            l = l.replaceAll("[\\[,\\];]", " ");

            var scanner = new Scanner(l);
            float x1 = scanner.nextInt();
            float y1 = scanner.nextInt();
            float x2 = scanner.nextInt();
            float y2 = scanner.nextInt();

            outList.add(createNewSlope(x1, y1, x2, y2, gl));
        }

        return outList;
    }

    private OpenGlObject createNewRectObject(int startX, int startY, int horSize, int vertSize, GL4 gl) {
        var out = new OpenGlObject(2, 6, gl,
                startX, startY, new Dimension(horSize, vertSize), 0) {
            @Override
            public void loadTexture(String filePath) {
                try {
                    this.texture = TextureLoader.loadTexture(filePath);
                    GameLabyrinth.initRepeatableTexParameters(this.texture, this.gl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        out.initRenderData(new String[]{this.getClass().
                        getClassLoader().
                        getResource("textures/labyrinth/abbey_base.jpg").
                        getPath()},
                false,
                new float[]{0f, 1f,
                        1f, 0f,
                        0f, 0f,
                        0f, 1f,
                        1f, 1f,
                        1f, 0f},
                new float[]{10f, 0f,
                        0f, 10f,
                        10f, 10f,
                        10f, 0f,
                        0f, 0f,
                        0f, 10f});

        return out;
    }

    private OpenGlObject createNewSlope(float x1, float y1, float x2, float y2, GL4 gl) {
        var out = new OpenGlObject(2, 3, gl, x1, y1,
                new Dimension((int)(x2 - x1), (int)(y1 - y2)), 0) {
            @Override
            public void loadTexture(String filePath) {
                try {
                    this.texture = TextureLoader.loadTexture(filePath);
                    GameLabyrinth.initRepeatableTexParameters(this.texture, this.gl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        System.out.println(x2/x1 + ", " + y2/y1);

        float[] vertices;

        //TODO: fix 2,3,4,
        if(x2 > x1 && y2 < y1)
            vertices = new float[]{0f, 1f, 1f, 1f, 1f, 0f};
        else if (x2 > x1 && y2 > y1)
            vertices = new float[]{0f, 0f, 0f, 1f, 1f, 0f};
        else if (x2 < x1 && y2 > y1)
            vertices = new float[]{1f,0f,0f,0f,1f,1f};
        else if (x2 < x1 && y2 < y1)
            vertices = new float[]{1f,0f,0f,0f,0f,1f};
        else
            vertices = new float[]{0f,0f,0f,0f,0f,0f};

        out.initRenderData(new String[]{this.getClass().
                        getClassLoader().
                        getResource("textures/labyrinth/abbey_base.jpg").
                        getPath()},
                false,
                vertices,
                vertices);

        return out;
    }
}
