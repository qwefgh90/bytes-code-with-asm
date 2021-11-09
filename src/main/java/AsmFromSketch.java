import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import static org.objectweb.asm.Opcodes.*;

public class AsmFromSketch {
    public static void main(String[] args) throws IOException {
        System.out.print("Class name? ");
        var scanner = new Scanner(System.in);
        var className = scanner.next().trim();
        //Define class A
        ClassWriter cw = new ClassWriter(0);
        cw.visit(49, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
        cw.visitSource(className + ".java", null);

        defineMain(cw, className);
        defineConstructor(cw);
        defineMethods(cw, className);

        cw.visitEnd();
        Files.write(Paths.get(className + ".class"), cw.toByteArray());

        //execute class A in Java
        var p = new ProcessBuilder("java", className).start();
        try(BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
        }
        try(BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream()))) {
            String line;
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
    static void defineConstructor(ClassWriter cw){
        //Declare a constructor
        var constructor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        //Call a parent constructor
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();
    }
    static void defineMain(ClassWriter cw, String className){
        //Define main method
        MethodVisitor main = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
        main.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        main.visitLdcInsn("main is called");
        main.visitMethodInsn(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V", false);

        //Call a constructor <init>
        main.visitTypeInsn(NEW, className);
        main.visitInsn(DUP); //One for the constructor and another for storing
        main.visitMethodInsn(INVOKESPECIAL, className, "<init>", "()V", false);
        main.visitVarInsn(ASTORE,1); // pop the top of stack and store it into the second on local variables

        //Call myMethod1()
        main.visitVarInsn(ALOAD, 1);
        main.visitMethodInsn(INVOKESPECIAL, className, "myMethod1", "()V", false);
        main.visitInsn(RETURN);
        main.visitMaxs(2, 2); // for two parameters, for one argument and one instance(object)
        //the end of main method
        main.visitEnd();
    }
    static void defineMethods(ClassWriter cw, String className){
        //myMethod1
        var mv1 = cw.visitMethod(ACC_PUBLIC, "myMethod1", "()V", null, null);
        mv1.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv1.visitLdcInsn("my method1 is called");
        mv1.visitMethodInsn(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V", false);
        mv1.visitVarInsn(ALOAD, 0);
        mv1.visitMethodInsn(INVOKEVIRTUAL, className, "myMethod2", "()V");
        mv1.visitInsn(RETURN);
        mv1.visitMaxs(2, 1);
        mv1.visitEnd();

        //myMethod2
        var mv2 = cw.visitMethod(ACC_PUBLIC, "myMethod2", "()V", null, null);
        mv2.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv2.visitLdcInsn("my method2 is called");
        mv2.visitMethodInsn(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V", false);
        mv2.visitInsn(RETURN);
        mv2.visitMaxs(2, 1);
        mv2.visitEnd();
    }
}