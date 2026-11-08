package net.scapeemulator.asm.bundler.trans;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.util.List;

/** Input and camera hooks for the original build-592 client. */
public final class MiddleMouseCameraTransformer extends Transformer implements Opcodes {

	private static final String HELPER = "net/scapeemulator/asm/bundler/input/MiddleMouseCamera";
	private int mouseHooks, cameraHooks, resetHooks;

	@Override
	@SuppressWarnings("unchecked")
	public void transform(ClassNode node) {
		for (MethodNode method : (List<MethodNode>) node.methods) {
			if (node.name.equals("gf") && method.name.startsWith("mouse")
					&& method.desc.equals("(Ljava/awt/event/MouseEvent;)V")) {
				InsnList hook = new InsnList();
				LabelNode original = new LabelNode();
				hook.add(new VarInsnNode(ALOAD, 1));
				hook.add(new MethodInsnNode(INVOKESTATIC, HELPER, "handle", "(Ljava/awt/event/MouseEvent;)Z"));
				hook.add(new JumpInsnNode(IFEQ, original));
				hook.add(new InsnNode(RETURN));
				hook.add(original);
				method.instructions.insert(hook);
				mouseHooks++;
			} else if (node.name.equals("no") && method.name.equals("b") && method.desc.equals("(I)V")) {
				InsnList hook = new InsnList();
				adjust(hook, "so", "c", "yaw");
				adjust(hook, "ii", "w", "pitch");
				method.instructions.insert(hook);
				cameraHooks++;
			} else if ((node.name.equals("o") && method.name.equals("focusLost")
					&& method.desc.equals("(Ljava/awt/event/FocusEvent;)V"))
					|| (node.name.equals("gf") && method.name.equals("b") && method.desc.equals("(Z)V"))) {
				method.instructions.insert(new MethodInsnNode(INVOKESTATIC, HELPER, "reset", "()V"));
				resetHooks++;
			}
		}
	}

	private static void adjust(InsnList hook, String owner, String field, String method) {
		hook.add(new FieldInsnNode(GETSTATIC, owner, field, "F"));
		hook.add(new MethodInsnNode(INVOKESTATIC, HELPER, method, "(F)F"));
		hook.add(new FieldInsnNode(PUTSTATIC, owner, field, "F"));
	}

	public void verify() {
		if (mouseHooks != 7 || cameraHooks != 1 || resetHooks != 2) {
			throw new IllegalStateException("Unexpected build-592 camera hooks: "
					+ mouseHooks + "/" + cameraHooks + "/" + resetHooks);
		}
	}
}
