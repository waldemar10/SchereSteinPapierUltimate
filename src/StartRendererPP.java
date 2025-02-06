package src;/*
 * Copyright 2012-2013 JogAmp Community. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY JogAmp Community ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL JogAmp Community OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of JogAmp Community.
 */

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.PMVMatrix;
import de.hshl.obj.loader.OBJLoader;
import de.hshl.obj.loader.Resource;


import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.jogamp.opengl.GL.GL_TEXTURE0;
import static com.jogamp.opengl.GL.GL_TEXTURE_2D;

/**
 * Performs the OpenGL rendering
 * Uses the programme pipeline commands in the core profile only.
 * Thus, a vertex and fragment shader is used.
 *
 * Rotation and translation of the camera is included.
 * 	    Use keyboard: left/right/up/down-keys and +/-Keys
 * 	    Alternatively use mouse movements:
 * 	        press left/right button and move and use mouse wheel
 *
 * Loads a model using an OBJ-Loader
 * Serves as a template (start code) for setting up an OpenGL/Jogl application
 * which is using a vertex and fragment shader in the core profile.
 *
 * Please make sure setting the file path and names of the shaders correctly (see below).
 *
 * Based on a tutorial by Chua Hock-Chuan
 * http://www3.ntu.edu.sg/home/ehchua/programming/opengl/JOGL2.0.html
 *
 * and on an example by Xerxes Rånby
 * http://jogamp.org/git/?p=jogl-demos.git;a=blob;f=src/demos/es2/RawGL2ES2demo.java;hb=HEAD
 *
 * @author Karsten Lehn
 * @version 3.9.2015, 15.9.2015, 18.9.2015, 10.9.2017, 2.10.2018, 7.10.2018
 *
 */
public class StartRendererPP extends GLCanvas implements GLEventListener {

    private static final long serialVersionUID = 1L;
    // Defining shader source code file paths and names
    final String shaderPath = ".\\resources\\";

    final String vertexShaderFileName0 = "Basic0.vert";
    final String fragmentShaderFileName0 = "Basic0.frag";

    final String vertexShaderFileName1 = "Basic1.vert";
    final String fragmentShaderFileName1 = "Basic1.frag";

    final String vertexShaderFileName2 = "BlinnPhongPoint.vert";
    final String fragmentShaderFileName2 = "BlinnPhongPoint.frag";

    private static final Path objFile = Paths.get("./resources/models/Berge.obj");


    // Object for loading shaders and creating a shader program
    private ShaderProgram shaderProgram0;
    private ShaderProgram shaderProgram1;
    private ShaderProgram shaderProgram2;

    // OpenGL buffer names for data allocation and handling on GPU
    int[] vaoName;  // List of names (integer pointers) of vertex array objects
    int[] vboName;  // List of names (integer pointers) of vertex buffer objects
    int[] iboName;

    // Declaration of an object for handling keyboard and mouse interactions
    InteractionHandler interactionHandler;

    // Declaration for using the projection-model-view matrix tool
    PMVMatrix pmvMatrix;

    // contains the geometry of our OBJ file
    float[] verticies0;

    //Cone Objects
    private Cone cone0;
    private Cone cone1;
    private Cone cone2;
    private Cone cone3;
    private Sphere sphere;

    private LightSource light0;
    private Material material1;
    private Material material0;

    //Bestimmt Farbe des Materials
    private final float[] matDiffuseRed =  {1f, 0.f, 0f, 1.0f};
    private final float[] matDiffuseBlue =  {0f, 0.f, 1f, 1.0f};

    //Anazhl an Objekte, die an die Grafikkarte geschickt werden
    private final int noOfObjects = 9;

    /**
     * Standard constructor for object creation.
     */
    public StartRendererPP() {
        // Create the OpenGL canvas with default capabilities
        super();
        // Add this object as OpenGL event listener to the canvas
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }

    /**
     * Create the canvas with the requested OpenGL capabilities
     * @param capabilities The capabilities of the canvas, including the OpenGL profile
     */
    public StartRendererPP(GLCapabilities capabilities) {
        // Create the OpenGL canvas with the requested OpenGL capabilities
        super(capabilities);
        // Add this object as an OpenGL event listener to the canvas
        this.addGLEventListener(this);
        createAndRegisterInteractionHandler();
    }

    /**
     * Helper method for creating an interaction handler object and registering it
     * for key press and mouse interaction call backs.
     */
    private void createAndRegisterInteractionHandler() {
        // The constructor call of the interaction handler generates meaningful default values.
        // The start parameters can also be set via setters
        // (see class definition of the interaction handler).
        interactionHandler = new InteractionHandler();
        this.addKeyListener(interactionHandler);
        this.addMouseListener(interactionHandler);
        this.addMouseMotionListener(interactionHandler);
        this.addMouseWheelListener(interactionHandler);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * that is called when the OpenGL renderer is started for the first time.
     * @param drawable The OpenGL drawable
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();
        // Outputs information about the available and chosen profile
        System.err.println("Chosen GLCapabilities: " + drawable.getChosenGLCapabilities());
        System.err.println("INIT GL IS: " + gl.getClass().getName());
        System.err.println("GL_VENDOR: " + gl.glGetString(GL.GL_VENDOR));
        System.err.println("GL_RENDERER: " + gl.glGetString(GL.GL_RENDERER));
        System.err.println("GL_VERSION: " + gl.glGetString(GL.GL_VERSION));

        // Verify if VBO-Support is available
        if(!gl.isExtensionAvailable("GL_ARB_vertex_buffer_object"))
            System.out.println("Error: VBO support is missing");
        else
            System.out.println("VBO support is available");

        // BEGIN: Preparing scene
        // BEGIN: Allocating vertex array objects and buffers for each object
        // create vertex array objects for noOfObjects objects (VAO)
        vaoName = new int[noOfObjects];
        gl.glGenVertexArrays(noOfObjects, vaoName, 0);
        if (vaoName[0] < 1)
            System.err.println("Error allocating vertex array object (VAO).");

        // create vertex buffer objects for noOfObjects objects (VBO)
        vboName = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, vboName, 0);
        if (vboName[0] < 1)
            System.err.println("Error allocating vertex buffer object (VBO).");

        // create index buffer objects for noOfObjects objects (IBO)
        iboName = new int[noOfObjects];
        gl.glGenBuffers(noOfObjects, iboName, 0);
        if (iboName[0] < 1)
            System.err.println("Error allocating index buffer object.");

        // END: Allocating vertex array objects and buffers for each object

        // Initialize objects to be drawn (see respective sub-methods)
        initObjectBerge(gl);
        initObjectHand(gl);
        initObjectPfeiler(gl);
        initObjectBoden(gl);
        initObjectDach(gl);
        initObjectFingerl3(gl);
        initObjectGelenk(gl);
        initObjectFingerl2(gl);
        initObjectFingerl1(gl);

        float[] lightPosition = {0.0f, 3.0f, 3.0f, 1.0f};
        float[] lightAmbientColor = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] lightDiffuseColor = {1.0f, 1.0f, 1.0f, 1.0f};
        float[] lightSpecularColor = {1.0f, 1.0f, 1.0f, 1.0f};
        light0 = new LightSource(lightPosition, lightAmbientColor,
                lightDiffuseColor, lightSpecularColor);

        pmvMatrix = new PMVMatrix();
        //Abstand von Kamera zu 0,0,0
        interactionHandler.setEyeZ(4f);

    }

    /**
     * Verticies werden von der .obj in einem Array gespeichert
     * @param gl
     */

    public void initObjectBerge(GL3 gl) {

        gl.glBindVertexArray(vaoName[0]);

        // Loading the vertex and fragment shaders and creation of the shader program.
        shaderProgram0 = new ShaderProgram(gl);
        shaderProgram0.loadShaderAndCreateProgram(shaderPath,
                vertexShaderFileName0, fragmentShaderFileName0);

        // Create object for projection-model-view matrix calculation.
        pmvMatrix = new PMVMatrix();

        // Vertices for drawing a triangle.
        // To be transferred to a vertex buffer object on the GPU.
        // Interleaved data layout: position, color
        try {
            verticies0 = new OBJLoader()
                    .setLoadNormals(true) // tell the loader to also load normal data
                    .loadMesh(Resource.file(objFile)) // actually load the file
                    .getVertices(); // take the vertices from the loaded mesh
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[0]);

        gl.glBufferData(GL.GL_ARRAY_BUFFER, verticies0.length * Float.BYTES,
                FloatBuffer.wrap(verticies0), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        // Map layout position 0 to the position information per vertex in the VBO.
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 6* Float.BYTES, 0);
        // Enable layout position 1
        gl.glEnableVertexAttribArray(1);
        // Map layout position 1 to the color information per vertex in the VBO.
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 6* Float.BYTES, 3* Float.BYTES);

        // Switch on depth test.
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    /**
     * Erstellung eines Quaders fuer die Hand
     * @param gl
     */
    //Hand mit jogl erstellt
    public void initObjectHand(GL3 gl){

        gl.glBindVertexArray(vaoName[1]);

        //Farbe
        float [] c1 = {0f, 0f, 1f};

        //Punkte
        float [] p1 = {-0.4f, -0.5f, 0.075f};
        float [] p2 = {0.4f, -0.5f, 0.075f};
        float [] p3 = {-0.4f, 0.5f, 0.075f};
        float [] p4 = {0.4f, 0.5f, 0.075f};

        float [] p5 = {-0.4f, -0.5f, -0.075f};
        float [] p6 = {0.4f, -0.5f, -0.075f};
        float [] p7 = {-0.4f, 0.5f, -0.075f};
        float [] p8 = {0.4f, 0.5f, -0.075f};
        //Normalenvektoren
        float [] n0 = {0f,0f,1f};
        float [] n1 = {0f,0f,-1f};
        float [] n2 = {0f,-1f,0f};
        float [] n3 = {1f,0f,0f};
        float [] n4 = {0f,1f,0f};
        float [] n5 = {-1f,0f,0f};


        float[] verticies = {
                //Vorderseite
                p1[0], p1[1], p1[2],
                c1[0], c1[1], c1[2],
                n0[0], n0[1], n0[2],
                p2[0], p2[1], p2[2],
                c1[0], c1[1], c1[2],
                n0[0], n0[1], n0[2],
                p3[0], p3[1], p3[2],
                c1[0], c1[1], c1[2],
                n0[0], n0[1], n0[2],

                p2[0], p2[1], p2[2],
                c1[0], c1[1], c1[2],
                n0[0], n0[1], n0[2],
                p3[0], p3[1], p3[2],
                c1[0], c1[1], c1[2],
                n0[0], n0[1], n0[2],
                p4[0], p4[1], p4[2],
                c1[0], c1[1], c1[2],
                n0[0], n0[1], n0[2],

                //Hinterseite
                p5[0], p5[1], p5[2],
                c1[0], c1[1], c1[2],
                n1[0], n1[1], n1[2],
                p6[0], p6[1], p6[2],
                c1[0], c1[1], c1[2],
                n1[0], n1[1], n1[2],
                p7[0], p7[1], p7[2],
                c1[0], c1[1], c1[2],
                n1[0], n1[1], n1[2],

                p6[0], p6[1], p6[2],
                c1[0], c1[1], c1[2],
                n1[0], n1[1], n1[2],
                p7[0], p7[1], p7[2],
                c1[0], c1[1], c1[2],
                n1[0], n1[1], n1[2],
                p8[0], p8[1], p8[2],
                c1[0], c1[1], c1[2],
                n1[0], n1[1], n1[2],

                //Unterseite
                p1[0], p1[1], p1[2],
                c1[0], c1[1], c1[2],
                n2[0], n2[1], n2[2],
                p5[0], p5[1], p5[2],
                c1[0], c1[1], c1[2],
                n2[0], n2[1], n2[2],
                p6[0], p6[1], p6[2],
                c1[0], c1[1], c1[2],
                n2[0], n2[1], n2[2],

                p1[0], p1[1], p1[2],
                c1[0], c1[1], c1[2],
                n2[0], n2[1], n2[2],
                p2[0], p2[1], p2[2],
                c1[0], c1[1], c1[2],
                n2[0], n2[1], n2[2],
                p6[0], p6[1], p6[2],
                c1[0], c1[1], c1[2],
                n2[0], n2[1], n2[2],

                //Seite rechts
                p2[0], p2[1], p2[2],
                c1[0], c1[1], c1[2],
                n3[0], n3[1], n3[2],
                p4[0], p4[1], p4[2],
                c1[0], c1[1], c1[2],
                n3[0], n3[1], n3[2],
                p8[0], p8[1], p8[2],
                c1[0], c1[1], c1[2],
                n3[0], n3[1], n3[2],

                p2[0], p2[1], p2[2],
                c1[0], c1[1], c1[2],
                n3[0], n3[1], n3[2],
                p6[0], p6[1], p6[2],
                c1[0], c1[1], c1[2],
                n3[0], n3[1], n3[2],
                p8[0], p8[1], p8[2],
                c1[0], c1[1], c1[2],
                n3[0], n3[1], n3[2],

                //Oberseite
                p3[0], p3[1], p3[2],
                c1[0], c1[1], c1[2],
                n4[0], n4[1], n4[2],
                p7[0], p7[1], p7[2],
                c1[0], c1[1], c1[2],
                n4[0], n4[1], n4[2],
                p8[0], p8[1], p8[2],
                c1[0], c1[1], c1[2],
                n4[0], n4[1], n4[2],

                p3[0], p3[1], p3[2],
                c1[0], c1[1], c1[2],
                n4[0], n4[1], n4[2],
                p4[0], p4[1], p4[2],
                c1[0], c1[1], c1[2],
                n4[0], n4[1], n4[2],
                p8[0], p8[1], p8[2],
                c1[0], c1[1], c1[2],
                n4[0], n4[1], n4[2],

                //Seite links
                p1[0], p1[1], p1[2],
                c1[0], c1[1], c1[2],
                n5[0], n5[1], n5[2],
                p3[0], p3[1], p3[2],
                c1[0], c1[1], c1[2],
                n5[0], n5[1], n5[2],
                p7[0], p7[1], p7[2],
                c1[0], c1[1], c1[2],
                n5[0], n5[1], n5[2],

                p1[0], p1[1], p1[2],
                c1[0], c1[1], c1[2],
                n5[0], n5[1], n5[2],
                p5[0], p5[1], p5[2],
                c1[0], c1[1], c1[2],
                n5[0], n5[1], n5[2],
                p7[0], p7[1], p7[2],
                c1[0], c1[1], c1[2],
                n5[0], n5[1], n5[2]

        };

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[1]);
        // floats use 4 bytes in Java

        gl.glBufferData(GL.GL_ARRAY_BUFFER, verticies.length * Float.BYTES,
                FloatBuffer.wrap(verticies), GL.GL_STATIC_DRAW);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9* Float.BYTES, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9* Float.BYTES, 3 * Float.BYTES);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9 * Float.BYTES, 6 * Float.BYTES);

        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    /**
     * Pfeiler fuer den Temepel
     * @param gl
     */
    //Pfeiler
    private void initObjectPfeiler(GL3 gl) {

        gl.glBindVertexArray(vaoName[2]);

        float[] color2 = {0.92f, 0.70f, 0.4f};
        cone0 = new Cone(64);
        float[] coneVertices = cone0.makeVertices(0.25f, 0.25f, 3f, color2);
        int[] coneIndices = cone0.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[2]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, coneVertices.length * 4,
                FloatBuffer.wrap(coneVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[2]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, coneIndices.length * 4,
                IntBuffer.wrap(coneIndices), GL.GL_STATIC_DRAW);

        // Metallic material
        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.6f, 0.53f, 0.43f, 1.0f};
        float[] matSpecular = {0.1f, 0.1f, 0.1f, 1.0f};
        float matShininess = 0.0f;

        material0 = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9 * Float.BYTES, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9 * Float.BYTES, 3 * Float.BYTES);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9 * Float.BYTES, 6 * Float.BYTES);

        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    /**
     * Boden der Szene
     * @param gl
     */
    //Boden
    private void initObjectBoden(GL3 gl) {

        gl.glBindVertexArray(vaoName[3]);
        shaderProgram1 = new ShaderProgram(gl);
        shaderProgram1.loadShaderAndCreateProgram(shaderPath,
                vertexShaderFileName1, fragmentShaderFileName1);

        //Farbe
        float[] c1 = {0.36f, 0.34f, 0.2f};

        //Punkte
        float[] p1 ={-3f, 0f, 4f};
        float[] p2 ={3f, 0f, 4f};
        float[] p3 ={-3f, 0f, -4f};
        float[] p4 ={3f, 0f, -4f};

        //Normalenvektor
        float[] n = {0f,1f,0f};

        float[] verticies = {
                //Dreieck links
                p1[0], p1[1], p1[2],    //Pos1
                c1[0], c1[1], c1[2],    //Farbe
                n[0],  n[1],  n[2],
                p2[0], p2[1], p2[2],    //Pos2
                c1[0], c1[1], c1[2],    //Farbe
                n[0],  n[1],  n[2],
                p3[0], p3[1], p3[2],    //Pos3
                c1[0], c1[1], c1[2],    //Farbe3
                n[0],  n[1],  n[2],
                //Dreieck rechts
                p2[0], p2[1], p2[2],    //Pos1
                c1[0], c1[1], c1[2],    //Farbe
                n[0],  n[1],  n[2],
                p3[0], p3[1], p3[2],    //Pos2
                c1[0], c1[1], c1[2],    //Farbe
                n[0],  n[1],  n[2],
                p4[0], p4[1], p4[2],    //Pos3
                c1[0], c1[1], c1[2],    //Farbe3
                n[0],  n[1],  n[2]
        };
        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[3]);
        // floats use 4 bytes in Java

        gl.glBufferData(GL.GL_ARRAY_BUFFER, verticies.length * Float.BYTES,
                FloatBuffer.wrap(verticies), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9* Float.BYTES, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9* Float.BYTES, 3 * Float.BYTES);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9* Float.BYTES, 3 * Float.BYTES);

        gl.glEnable(GL.GL_DEPTH_TEST);

    }

    /**
     * Erstellen eines Prismas als Dach
     * @param gl
     */
    //Dach
    private void initObjectDach(GL3 gl) {

        gl.glBindVertexArray(vaoName[4]);

        //Farbe
        float [] c1 = {0.75f, 0.67f, 0.55f};

        //Punkte
        float [] p1 = {3.05f, 1f, -4.05f};
        float [] p2 = {-3.05f, 1f, -4.05f};
        float [] p3 = {0f, 2.25f, -4.05f};
        float [] p4 = {0f, 2.25f, 4.05f};
        float [] p5 = {-3.05f, 1f, 4.05f};
        float [] p6 = {3.05f, 1f, 4.05f};

        //Normalenvektoren
        float [] n1 ={0f,0f,-1f};
        float [] n2 ={0f,0f,1f};
        float [] n3 ={0f,-1f,0f};
        float [] n4 ={10.125f, 24.705f, 0f};
        float [] n5 ={-10,125, 24.705f, 0f};

        float[] verticies = {
                //Dreieck hinten
                p1[0], p1[1], p1[2],    //Pos1
                c1[0], c1[1], c1[2],    //Farbe
                n1[0], n1[1], n1[2],
                p2[0], p2[1], p2[2],    //Pos2
                c1[0], c1[1], c1[2],    //Farbe
                n1[0], n1[1], n1[2],
                p3[0], p3[1], p3[2],    //Pos3
                c1[0], c1[1], c1[2],    //Farbe3
                n1[0], n1[1], n1[2],

                //Dreieck Dach rechts oben
                p1[0], p1[1], p1[2],    //Pos1
                c1[0], c1[1], c1[2],    //Farbe
                n4[0], n4[1], n4[2],
                p3[0], p3[1], p3[2],    //Pos2
                c1[0], c1[1], c1[2],    //Farbe
                n4[0], n4[1], n4[2],
                p4[0], p4[1], p4[2],    //Pos3
                c1[0], c1[1], c1[2],    //Farbe3
                n4[0], n4[1], n4[2],

                //Dreieck Dach rechts unten
                p4[0], p4[1], p4[2],    //Pos1
                c1[0], c1[1], c1[2],    //Farbe
                n4[0], n4[1], n4[2],
                p1[0], p1[1], p1[2],    //Pos2
                c1[0], c1[1], c1[2],    //Farbe
                n4[0], n4[1], n4[2],
                p6[0], p6[1], p6[2],    //Pos3
                c1[0], c1[1], c1[2],    //Farbe3
                n4[0], n4[1], n4[2],

                //Dreieck vorne
                p4[0], p4[1], p4[2],    //Pos1
                c1[0], c1[1], c1[2],    //Farbe
                n2[0], n2[1], n2[2],
                p5[0], p5[1], p5[2],    //Pos2
                c1[0], c1[1], c1[2],    //Farbe
                n2[0], n2[1], n2[2],
                p6[0], p6[1], p6[2],    //Pos3
                c1[0], c1[1], c1[2],    //Farbe3
                n2[0], n2[1], n2[2],

                //Dreieck unterseite rechts
                p1[0], p1[1], p1[2],    //Pos1
                c1[0], c1[1], c1[2],    //Farbe
                n3[0], n3[1], n3[2],
                p5[0], p5[1], p5[2],    //Pos2
                c1[0], c1[1], c1[2],    //Farbe
                n3[0], n3[1], n3[2],
                p6[0], p6[1], p6[2],    //Pos3
                c1[0], c1[1], c1[2],    //Farbe3
                n3[0], n3[1], n3[2],

                //Dreieck unterseite links
                p2[0], p2[1], p2[2],    //Pos1
                c1[0], c1[1], c1[2],    //Farbe
                n3[0], n3[1], n3[2],
                p5[0], p5[1], p5[2],    //Pos2
                c1[0], c1[1], c1[2],    //Farbe
                n3[0], n3[1], n3[2],
                p1[0], p1[1], p1[2],    //Pos3
                c1[0], c1[1], c1[2],    //Farbe3
                n3[0], n3[1], n3[2],

                //Dreieck Dach links oben
                p2[0], p2[1], p2[2],    //Pos1
                c1[0], c1[1], c1[2],    //Farbe
                n5[0], n5[1], n5[2],
                p3[0], p3[1], p3[2],    //Pos2
                c1[0], c1[1], c1[2],    //Farbe
                n5[0], n5[1], n5[2],
                p4[0], p4[1], p4[2],    //Pos3
                c1[0], c1[1], c1[2],    //Farbe3
                n5[0], n5[1], n5[2],

                //Dreieck Dach links unten
                p2[0], p2[1], p2[2],    //Pos1
                c1[0], c1[1], c1[2],    //Farbe
                n5[0], n5[1], n5[2],
                p5[0], p5[1], p5[2],    //Pos2
                c1[0], c1[1], c1[2],    //Farbe
                n5[0], n5[1], n5[2],
                p4[0], p4[1], p4[2],    //Pos3
                c1[0], c1[1], c1[2],    //Farbe3
                n5[0], n5[1], n5[2]
        };

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[4]);
        // floats use 4 bytes in Java

        gl.glBufferData(GL.GL_ARRAY_BUFFER, verticies.length * Float.BYTES,
                FloatBuffer.wrap(verticies), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9* Float.BYTES, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9* Float.BYTES, 3 * Float.BYTES);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9* Float.BYTES, 3 * Float.BYTES);

        // Metallic material
        float[] matEmission = {0.0f, 0.0f, 0.0f, 1.0f};
        float[] matAmbient =  {0.2f, 0.2f, 0.2f, 1.0f};
        float[] matDiffuse =  {0.33f, 0.24f, 0.14f, 1.0f};
        float[] matSpecular = {0.1f, 0.1f, 0.1f, 1.0f};
        float matShininess = 500.0f;

        material1 = new Material(matEmission, matAmbient, matDiffuse, matSpecular, matShininess);

        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    /**
     * Erstellt eine Sphaere, die als Gelenk der Hand dient
     * @param gl
     */
    private void initObjectGelenk(GL3 gl) {

        gl.glBindVertexArray(vaoName[6]);
        shaderProgram2 = new ShaderProgram(gl);
        shaderProgram2.loadShaderAndCreateProgram(shaderPath,
                vertexShaderFileName2, fragmentShaderFileName2);

        float[] color0 = {0.8f, 0.1f, 0.1f};
        sphere = new Sphere(64, 64);
        float[] sphereVertices = sphere.makeVertices(0.075f, color0);
        int[] sphereIndices = sphere.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[6]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, sphereVertices.length * 4,
                FloatBuffer.wrap(sphereVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[6]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sphereIndices.length * 4,
                IntBuffer.wrap(sphereIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // Defining input variables for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9 * Float.BYTES, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9 * Float.BYTES, 3 * Float.BYTES);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9 * Float.BYTES, 6 * Float.BYTES);
        // END: Prepare sphere for drawing

        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    /**
     * Eines von 3 Fingerstuecke, langes Stueck
     * @param gl
     */
    //Zylinder mit der Laenge 0.3
    private void initObjectFingerl3(GL3 gl) {

        gl.glBindVertexArray(vaoName[5]);

        float[] color2 = {0f, 0.6f, 0f};
        cone1 = new Cone(64);
        float[] coneVertices = cone1.makeVertices(0.075f, 0.075f, 0.3f, color2);
        int[] coneIndices = cone1.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[5]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, coneVertices.length * 4,
                FloatBuffer.wrap(coneVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[5]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, coneIndices.length * 4,
                IntBuffer.wrap(coneIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9 * Float.BYTES, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9 * Float.BYTES, 3 * Float.BYTES);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9 * Float.BYTES, 6 * Float.BYTES);
        // END: Prepare cube for drawing
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    /**
     * Eines von 3 Fingerstuecke, mittel langes Stueck
     * @param gl
     */
    //Zylinder mit der Laenge 0.2
    private void initObjectFingerl2(GL3 gl) {

        gl.glBindVertexArray(vaoName[7]);

        float[] color2 = {0f, 0.6f, 0f};
        cone2 = new Cone(64);
        float[] coneVertices = cone2.makeVertices(0.075f, 0.075f, 0.2f, color2);
        int[] coneIndices = cone2.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[7]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, coneVertices.length * 4,
                FloatBuffer.wrap(coneVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[7]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, coneIndices.length * 4,
                IntBuffer.wrap(coneIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9 * 4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9 * 4, 3 * 4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9 * 4, 6 * 4);
        // END: Prepare cube for drawing
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    /**
     * Eines von 3 Fingerstuecke, kurzes Stueck
     * @param gl
     */
    //Zylinder mit der Laenge 0.15
    private void initObjectFingerl1(GL3 gl) {

        gl.glBindVertexArray(vaoName[8]);

        float[] color2 = {0f, 0.6f, 0f};
        cone3 = new Cone(64);
        float[] coneVertices = cone3.makeVertices(0.075f, 0.075f, 0.15f, color2);
        int[] coneIndices = cone3.makeIndicesForTriangleStrip();

        // activate and initialize vertex buffer object (VBO)
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vboName[8]);
        // floats use 4 bytes in Java
        gl.glBufferData(GL.GL_ARRAY_BUFFER, coneVertices.length * 4,
                FloatBuffer.wrap(coneVertices), GL.GL_STATIC_DRAW);

        // activate and initialize index buffer object (IBO)
        gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, iboName[8]);
        // integers use 4 bytes in Java
        gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, coneIndices.length * 4,
                IntBuffer.wrap(coneIndices), GL.GL_STATIC_DRAW);

        // Activate and order vertex buffer object data for the vertex shader
        // The vertex buffer contains: position (3), color (3), normals (3)
        // Defining input for vertex shader
        // Pointer for the vertex shader to the position information per vertex
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false, 9 * 4, 0);
        // Pointer for the vertex shader to the color information per vertex
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL.GL_FLOAT, false, 9 * 4, 3 * 4);
        // Pointer for the vertex shader to the normal information per vertex
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL.GL_FLOAT, false, 9 * 4, 6 * 4);
        // END: Prepare cube for drawing
        gl.glEnable(GL.GL_DEPTH_TEST);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called by the OpenGL animator for every frame.
     * @param drawable The OpenGL drawable
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        AnimationGroups a = new AnimationGroups();
        Animations animations = new Animations();
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();
        // Clear color and depth buffer
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        //Background Color
        gl.glClearColor(0.28f, 0.51f, 0.71f, 1.0f);

        // Controlling the interaction settings
/*        System.out.println("Camera: z = " + interactionHandler.getEyeZ() + ", " +
                "x-Rot: " + interactionHandler.getAngleXaxis() +
                ", y-Rot: " + interactionHandler.getAngleYaxis() +
                ", x-Translation: " + interactionHandler.getxPosition()+
                ", y-Translation: " + interactionHandler.getyPosition());// definition of translation of model (Model/Object Coordinates --> World Coordinates)
*/

        // Apply view transform using the PMV-Tool
        // Camera positioning is steered by the interaction handler
        pmvMatrix.glLoadIdentity();
        pmvMatrix.gluLookAt(2.5f, 1f, interactionHandler.getEyeZ(),
                0f, 0f, 0f,
                0f, 1.0f, 0f);
        pmvMatrix.glTranslatef(interactionHandler.getxPosition(), interactionHandler.getyPosition(), 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleXaxis(), 1f, 0f, 0f);
        pmvMatrix.glRotatef(interactionHandler.getAngleYaxis(), 0f, 1f, 0f);

        //Abstand Hand-Knöchel: 0.075; Finger1: 0.225; Finger2: 0.175; Finger3: 0.15

        //Hand des Spielers
        //Handfläche
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0f,-0.5f,2f);
        a.animationsHand(1, pmvMatrix);
        displayObjectHand(gl, matDiffuseRed);
        pmvMatrix.glPopMatrix();

        //Kleinerfinger
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-0.325f, 0.075f,2f); //Gelenk1
        a.animationsKleinerFinger1(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück1
        displayObjectFingerl3(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Gelenk2
        a.animationsKleinerFinger2(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück2
        displayObjectFingerl3(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Gelenk3
        a.animationsKleinerFinger3(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück3
        displayObjectFingerl3(gl, matDiffuseRed);
        pmvMatrix.glPopMatrix();

        //Ringfinger
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-0.109f, 0.075f,2f); //Gelenk1
        a.animationsRingFinger1(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück1
        displayObjectFingerl2(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk2
        a.animationsRingFinger2(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück2
        displayObjectFingerl2(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk3
        a.animationsRingFinger3(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück3
        displayObjectFingerl3(gl, matDiffuseRed);
        pmvMatrix.glPopMatrix();

        //Mittelfinger
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0.109f, 0.075f,2f);  //Gelenk1
        a.animationsMittelFinger1(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.225f,0f);      //Fingerstück1
        displayObjectFingerl1(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.225f,0f);      //Gelenk2
        a.animationsMittelFinger2(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück2
        displayObjectFingerl2(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk3
        a.animationsMittelFinger3(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück3
        displayObjectFingerl3(gl, matDiffuseRed);
        pmvMatrix.glPopMatrix();

        //Zeigefinger
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0.325f, 0.075f,2f);  //Gelenk1
        a.animationsZeigeFinger1(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück1
        displayObjectFingerl2(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk2
        a.animationsZeigeFinger2(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstck2
        displayObjectFingerl2(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk3
        a.animationsZeigeFinger3(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück3
        displayObjectFingerl3(gl, matDiffuseRed);
        pmvMatrix.glPopMatrix();

        //Daumen
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0.475f, -0.4f,2f);   //Gelenk1
        pmvMatrix.glRotatef(-30f,0f,0f,1f);
        a.animationsDaumen1(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück1
        displayObjectFingerl2(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk2
        a.animationsDaumen2(1, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseRed);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück2
        displayObjectFingerl2(gl, matDiffuseRed);
        pmvMatrix.glPopMatrix();

        //Hand des Computergegners
        //Handfläche
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0f,-0.5f,-2f);
        a.animationsHand(2, pmvMatrix);
        displayObjectHand(gl, matDiffuseBlue);
        pmvMatrix.glPopMatrix();

        //Kleinerfinger
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0.325f, 0.075f,-2f); //Gelenk1
        a.animationsKleinerFinger1(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück1
        displayObjectFingerl3(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Gelenk2
        a.animationsKleinerFinger2(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück2
        displayObjectFingerl3(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Gelenk3
        a.animationsKleinerFinger3(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück3
        displayObjectFingerl3(gl, matDiffuseBlue);
        pmvMatrix.glPopMatrix();

        //Ringfinger
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0.109f, 0.075f,-2f); //Gelenk1
        a.animationsRingFinger1(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück1
        displayObjectFingerl2(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk2
        a.animationsRingFinger2(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück2
        displayObjectFingerl2(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk3
        a.animationsRingFinger3(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück3
        displayObjectFingerl3(gl, matDiffuseBlue);
        pmvMatrix.glPopMatrix();

        //Mittelfinger
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-0.109f, 0.075f,-2f);  //Gelenk1
        a.animationsMittelFinger1(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.225f,0f);      //Fingerstück1
        displayObjectFingerl1(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.225f,0f);      //Gelenk2
        a.animationsMittelFinger2(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück2
        displayObjectFingerl2(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk3
        a.animationsMittelFinger3(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück3
        displayObjectFingerl3(gl, matDiffuseBlue);
        pmvMatrix.glPopMatrix();

        //Zeigefinger
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-0.325f, 0.075f,-2f);  //Gelenk1
        a.animationsZeigeFinger1(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück1
        displayObjectFingerl2(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk2
        a.animationsZeigeFinger2(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstck2
        displayObjectFingerl2(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk3
        a.animationsZeigeFinger3(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.15f,0f);       //Fingerstück3
        displayObjectFingerl3(gl, matDiffuseBlue);
        pmvMatrix.glPopMatrix();

        //Daumen
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-0.475f, -0.4f,-2f);   //Gelenk1
        pmvMatrix.glRotatef(30f,0f,0f,1f);
        a.animationsDaumen1(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück1
        displayObjectFingerl2(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Gelenk2
        a.animationsDaumen2(2, pmvMatrix);
        displayObjectGelenk(gl, matDiffuseBlue);
        pmvMatrix.glTranslatef(0f, 0.175f,0f);      //Fingerstück2
        displayObjectFingerl2(gl, matDiffuseBlue);
        pmvMatrix.glPopMatrix();

        //Pfeiler vorne rechts ecke
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(2.75f, 0f, 3.75f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();
        //Pfeiler vorne rechs
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(1.375f, -0.45f, 3.75f);
        pmvMatrix.glScalef(1f,0.7f,1f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();
        //abgebrochenes Teil
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(1.375f, -1.25f, 2.875f);
        pmvMatrix.glRotatef(90f,0.5f,0f,1f);
        pmvMatrix.glScalef(1f,0.3f,1f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();
        //Pfeiler vorne mitte
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0f, 0f, 3.75f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();
        //Pfeiler vorne links
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-1.375f, 0f, 3.75f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();
        //Pfeiler vorne links ecke
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-2.75f, 0f, 3.75f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        //Pfeiler hinten rechts ecke
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(2.75f, 0f, -3.75f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();
        //Pfeiler hinten rechts
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(1.375f, 0f, -3.75f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();
        //Pfeiler hinten mitte
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0f, 0f, -3.75f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();
        //Pfeiler hinten links
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-1.375f, 0f, -3.75f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();
        //Pfeiler hinten links ecke
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-2.75f, 0f, -3.75f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        //Pfeiler rechte seite
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(2.75f, 0f, 2.5f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(2.75f, -1.275f, 1.25f);
        pmvMatrix.glScalef(1f,0.15f,1f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();
        //kaputtes Teil 1
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(4f, -1.25f, 0.65f);
        pmvMatrix.glRotatef(90f,0.2f,0f,1f);
        pmvMatrix.glScalef(1f,0.55f,1f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(2.75f, -0.9f, 0f);
        pmvMatrix.glScalef(1f,0.4f,1f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(2.75f, 0f, -1.25f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(2.75f, 0f, -2.5f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        //Pfeiler linke seite
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-2.75f, 0f, 2.5f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-2.75f, 0f, 1.25f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-2.75f, 0f, 0f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-2.75f, 0f, -1.25f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(-2.75f, -0.84f, -2.5f);
        pmvMatrix.glScalef(1f,0.44f,1f);
        displayObjectPfeiler(gl);
        pmvMatrix.glPopMatrix();

        //Boden
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0f, -1.5f, 0f);
        pmvMatrix.glScalef(3f,3f,3f);
        displayObjectBoden(gl);
        pmvMatrix.glPopMatrix();

        //Dach
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0f, 0.5f, 0f);
        displayObjectDach(gl);
        pmvMatrix.glPopMatrix();

        //Berge
        pmvMatrix.glPushMatrix();
        pmvMatrix.glTranslatef(0.5f,-1.8f,0f);
        pmvMatrix.glScalef(3.5f,3.5f,3.5f);
        displayObjectBerge(gl);
        pmvMatrix.glPopMatrix();

        a.counting();
    }

    private void displayObjectBerge(GL3 gl) {
        gl.glUseProgram(shaderProgram0.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix)
        // to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glBindVertexArray(vaoName[0]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, verticies0.length);
    }

    private void displayObjectHand(GL3 gl, float[] matColor) {
        gl.glUseProgram(shaderProgram2.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glUniformMatrix4fv(2, 1, false, pmvMatrix.glGetMvitMatrixf());
        gl.glUniform4fv(3, 1, light0.getPosition(), 0);
        gl.glUniform4fv(4, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(5, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, light0.getSpecular(), 0);
        // transfer material parameters

        gl.glUniform4fv(7, 1, material0.getEmission(), 0);
        gl.glUniform4fv(8, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(9, 1, matColor, 0);
        gl.glUniform4fv(10, 1, material0.getSpecular(), 0);
        gl.glUniform1f(11, material0.getShininess());
        gl.glBindVertexArray(vaoName[1]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);
    }

    private void displayObjectPfeiler(GL3 gl) {
        gl.glUseProgram(shaderProgram2.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glUniformMatrix4fv(2, 1, false, pmvMatrix.glGetMvitMatrixf());
        gl.glUniform4fv(3, 1, light0.getPosition(), 0);
        gl.glUniform4fv(4, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(5, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, light0.getSpecular(), 0);
        // transfer material parameters

        gl.glUniform4fv(7, 1, material0.getEmission(), 0);
        gl.glUniform4fv(8, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(9, 1, material0.getDiffuse(), 0);
        gl.glUniform4fv(10, 1, material0.getSpecular(), 0);
        gl.glUniform1f(11, material0.getShininess());
        gl.glBindVertexArray(vaoName[2]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, cone1.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    private void displayObjectBoden(GL3 gl) {
        gl.glUseProgram(shaderProgram1.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[3]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawArrays(GL.GL_TRIANGLES,0, 6);
    }

    private void displayObjectDach(GL3 gl) {
        gl.glUseProgram(shaderProgram1.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());

        gl.glBindVertexArray(vaoName[4]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 24);
    }

    /**
     *
     * @param gl
     * @param matColor bestimmt die Materialfarbe
     */
    private void displayObjectFingerl1(GL3 gl, float[] matColor) {
        gl.glUseProgram(shaderProgram2.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glUniformMatrix4fv(2, 1, false, pmvMatrix.glGetMvitMatrixf());
        gl.glUniform4fv(3, 1, light0.getPosition(), 0);
        gl.glUniform4fv(4, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(5, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, light0.getSpecular(), 0);
        // transfer material parameters

        gl.glUniform4fv(7, 1, material0.getEmission(), 0);
        gl.glUniform4fv(8, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(9, 1, matColor, 0);
        gl.glUniform4fv(10, 1, material0.getSpecular(), 0);
        gl.glUniform1f(11, material0.getShininess());
        gl.glBindVertexArray(vaoName[5]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, cone1.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    /**
     *
     * @param gl
     * @param matColor bestimmt die Materialfarbe
     */
    private void displayObjectGelenk(GL3 gl, float [] matColor) {
        gl.glUseProgram(shaderProgram2.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix)
        // to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glUniformMatrix4fv(2, 1, false, pmvMatrix.glGetMvitMatrixf());
        gl.glUniform4fv(3, 1, light0.getPosition(), 0);
        gl.glUniform4fv(4, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(5, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, light0.getSpecular(), 0);
        // transfer material parameters

        gl.glUniform4fv(7, 1, material0.getEmission(), 0);
        gl.glUniform4fv(8, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(9, 1, matColor, 0);
        gl.glUniform4fv(10, 1, material0.getSpecular(), 0);
        gl.glUniform1f(11, material0.getShininess());
        gl.glBindVertexArray(vaoName[6]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, sphere.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    private void displayObjectFingerl2(GL3 gl, float[] matColor) {
        gl.glUseProgram(shaderProgram2.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glUniformMatrix4fv(2, 1, false, pmvMatrix.glGetMvitMatrixf());
        gl.glUniform4fv(3, 1, light0.getPosition(), 0);
        gl.glUniform4fv(4, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(5, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, light0.getSpecular(), 0);
        // transfer material parameters

        gl.glUniform4fv(7, 1, material0.getEmission(), 0);
        gl.glUniform4fv(8, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(9, 1, matColor, 0);
        gl.glUniform4fv(10, 1, material0.getSpecular(), 0);
        gl.glUniform1f(11, material0.getShininess());
        gl.glBindVertexArray(vaoName[7]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, cone1.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    private void displayObjectFingerl3(GL3 gl, float [] matColor) {
        gl.glUseProgram(shaderProgram2.getShaderProgramID());
        // Transfer the PVM-Matrix (model-view and projection matrix) to the vertex shader
        gl.glUniformMatrix4fv(0, 1, false, pmvMatrix.glGetPMatrixf());
        gl.glUniformMatrix4fv(1, 1, false, pmvMatrix.glGetMvMatrixf());
        gl.glUniformMatrix4fv(2, 1, false, pmvMatrix.glGetMvitMatrixf());
        gl.glUniform4fv(3, 1, light0.getPosition(), 0);
        gl.glUniform4fv(4, 1, light0.getAmbient(), 0);
        gl.glUniform4fv(5, 1, light0.getDiffuse(), 0);
        gl.glUniform4fv(6, 1, light0.getSpecular(), 0);
        // transfer material parameters

        gl.glUniform4fv(7, 1, material0.getEmission(), 0);
        gl.glUniform4fv(8, 1, material0.getAmbient(), 0);
        gl.glUniform4fv(9, 1, matColor, 0);
        gl.glUniform4fv(10, 1, material0.getSpecular(), 0);
        gl.glUniform1f(11, material0.getShininess());
        gl.glBindVertexArray(vaoName[8]);
        // Draws the elements in the order defined by the index buffer object (IBO)
        gl.glDrawElements(GL.GL_TRIANGLE_STRIP, cone1.getNoOfIndices(), GL.GL_UNSIGNED_INT, 0);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when the OpenGL window is resized.
     * @param drawable The OpenGL drawable
     * @param x x-coordinate of the viewport
     * @param y y-coordinate of the viewport
     * @param width width of the viewport
     * @param height height of the viewport
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();

        // Set the viewport to the entire window
        gl.glViewport(0, 0, width, height);
        // Switch the pmv-tool to perspective projection
        pmvMatrix.glMatrixMode(PMVMatrix.GL_PROJECTION);
        // Reset projection matrix to identity
        pmvMatrix.glLoadIdentity();
        // Calculate projection matrix
        //      Parameters:
        //          fovy (field of view), aspect ratio,
        //          zNear (near clipping plane), zFar (far clipping plane)
        pmvMatrix.gluPerspective(45f, (float) width/ (float) height, 0.1f, 10000f);
        // Switch to model-view transform
        pmvMatrix.glMatrixMode(PMVMatrix.GL_MODELVIEW);
    }

    /**
     * Implementation of the OpenGL EventListener (GLEventListener) method
     * called when OpenGL canvas ist destroyed.
     * @param drawable the drawable
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
        // Retrieve the OpenGL graphics context
        GL3 gl = drawable.getGL().getGL3();
        System.out.println("Deleting allocated objects, incl. the shader program.");

        // Detach and delete shader program
        gl.glUseProgram(0);
        shaderProgram0.deleteShaderProgram();
        shaderProgram1.deleteShaderProgram();
        shaderProgram2.deleteShaderProgram();


        // deactivate VAO and VBO
        gl.glBindVertexArray(0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDeleteVertexArrays(1, vaoName,0);
        gl.glDeleteBuffers(1, vboName, 0);

        System.exit(0);
    }
}