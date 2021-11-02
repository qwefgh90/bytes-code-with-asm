import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.objectweb.asm.Opcodes.*;

public class AsmFromSketch {
    public static void main(String[] args) throws IOException {
        System.out.println("hello world!");
        ClassWriter cw = new ClassWriter(0);
        cw.visit(49, ACC_PUBLIC + ACC_SUPER, "A", null, "java/lang/Object", null);
        cw.visitSource("A.java", null);
        cw.visitEnd();
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
                "([Ljava/lang/String;)V", null, null);
        mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("myMethod1 is called");
        mv.visitMethodInsn(INVOKEVIRTUAL,"java/io/PrintStream","println","(Ljava/lang/String;)V");
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
        Files.write(Paths.get("A.class"), cw.toByteArray());
    }
}