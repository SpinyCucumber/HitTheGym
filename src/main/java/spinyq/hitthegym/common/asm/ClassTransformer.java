package spinyq.hitthegym.common.asm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class ClassTransformer implements IClassTransformer, Opcodes {

	public static class MethodSignature {
		private final String funcName, srgName, funcDesc;

		public MethodSignature(String funcName, String srgName, String funcDesc) {
			this.funcName = funcName;
			this.srgName = srgName;
			this.funcDesc = funcDesc;
		}

		@Override
		public String toString() {
			return "Names [" + funcName + ", " + srgName + "] Descriptor " + funcDesc;
		}

		public boolean matches(String methodName, String methodDesc) {
			return (methodName.equals(funcName) || methodName.equals(srgName))
					&& (methodDesc.equals(funcDesc));
		}

		public boolean matches(MethodNode method) {
			return matches(method.name, method.desc);
		}

		public boolean matches(MethodInsnNode method) {
			return matches(method.name, method.desc);
		}

		public String mappedName(String owner) {
			return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(owner, srgName, funcDesc);
		}

	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (name.equals("net.minecraft.client.model.ModelBiped")) {
			return transformModelBiped(basicClass);
		}
		return basicClass;
	}

	private static byte[] transformModelBiped(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("setRotationAngles", "func_78087_a", "(FFFFFFLnet/minecraft/entity/Entity;)V");

		return transform(basicClass, forMethod(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == RETURN;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(ALOAD, 7));
					newInstructions.add(new MethodInsnNode(INVOKESTATIC, "spinyq/hitthegym/client/RenderHandler", "setRotationAngles", "(Lnet/minecraft/entity/Entity;)V", false));

					method.instructions.insertBefore(node, newInstructions);
					return true;
				})));
	}
	
	// BOILERPLATE BELOW ==========================================================================================================================================

		private static boolean debugLog = false;

		private static byte[] transform(byte[] basicClass, TransformerAction... methods) {
			ClassReader reader;
			try {
				reader = new ClassReader(basicClass);
			} catch (NullPointerException ex) {
				return basicClass;
			}

			ClassNode node = new ClassNode();
			reader.accept(node, 0);
			if (debugLog)
				log(getNodeString(node));

			boolean didAnything = false;

			for (TransformerAction pair : methods)
				didAnything |= pair.test(node);

			if (didAnything) {
				ClassWriter writer = new SafeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
				if (debugLog)
					log(getNodeString(node));
				debugLog = false;
				node.accept(writer);
				return writer.toByteArray();
			}

			return basicClass;
		}

		public static boolean findMethodAndTransform(ClassNode node, MethodSignature sig, MethodAction predicate, boolean logResult) {
			for (MethodNode method : node.methods) {
				if (sig.matches(method)) {
					log("Located Method, patching...");

					boolean finish = predicate.test(method);
					if (logResult)
						log("Patch result: " + finish);

					debugMethod(method, finish);

					return finish;
				}
			}

			if (logResult)
				log("Failed to locate the method!");
			return false;
		}

		public static void debugMethod(MethodNode node, boolean result) {
			if (!result) {
				String nodeString = getNodeString(node);
				log("============= Please report this! =============\n" + nodeString.substring(0, nodeString.length() - 1));
				log("============= Please report this! =============");
			}
		}

		public static MethodAction combine(NodeFilter filter, NodeAction action) {
			return (MethodNode node) -> applyOnNode(node, filter, action);
		}

		public static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
			Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

			boolean didAny = false;
			while (iterator.hasNext()) {
				AbstractInsnNode anode = iterator.next();
				if (filter.test(anode)) {
					log("Located patch target node " + getNodeString(anode));
					didAny = true;
					if (action.test(method, anode))
						break;
				}
			}

			return didAny;
		}

		private static void log(String str) {
			LogManager.getLogger("HitTheGymASM").info(str);
		}

		private static String getNodeString(ClassNode node) {
			StringWriter sw = new StringWriter();
			PrintWriter printer = new PrintWriter(sw);

			TraceClassVisitor visitor = new TraceClassVisitor(printer);
			node.accept(visitor);

			return sw.toString();
		}

		private static String getNodeString(MethodNode node) {
			Printer printer = new Textifier();

			TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
			node.accept(visitor);

			StringWriter sw = new StringWriter();
			printer.print(new PrintWriter(sw));
			printer.getText().clear();

			return sw.toString();
		}


		private static String getNodeString(AbstractInsnNode node) {
			Printer printer = new Textifier();

			TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
			node.accept(visitor);

			StringWriter sw = new StringWriter();
			printer.print(new PrintWriter(sw));
			printer.getText().clear();

			return sw.toString().replaceAll("\n", "").trim();
		}
		
		/**
		 * Safe class writer.
		 * The way COMPUTE_FRAMES works may require loading additional classes. This can cause ClassCircularityErrors.
		 * The override for getCommonSuperClass will ensure that COMPUTE_FRAMES works properly by using the right ClassLoader.
		 * <p>
		 * Code from: https://github.com/JamiesWhiteShirt/clothesline/blob/master/src/core/java/com/jamieswhiteshirt/clothesline/core/SafeClassWriter.java
		 */
		public static class SafeClassWriter extends ClassWriter {
			public SafeClassWriter(int flags) {
				super(flags);
			}

			@Override
			protected String getCommonSuperClass(String type1, String type2) {
				Class<?> c, d;
				ClassLoader classLoader = Launch.classLoader;
				try {
					c = Class.forName(type1.replace('/', '.'), false, classLoader);
					d = Class.forName(type2.replace('/', '.'), false, classLoader);
				} catch (Exception e) {
					throw new RuntimeException(e.toString());
				}
				if (c.isAssignableFrom(d)) {
					return type1;
				}
				if (d.isAssignableFrom(c)) {
					return type2;
				}
				if (c.isInterface() || d.isInterface()) {
					return "java/lang/Object";
				} else {
					do {
						c = c.getSuperclass();
					} while (!c.isAssignableFrom(d));
					return c.getName().replace('.', '/');
				}
			}
		}

		private interface MethodAction extends Predicate<MethodNode> {
			// NO-OP
		}

		private interface NodeFilter extends Predicate<AbstractInsnNode> {
			// NO-OP
		}

		private interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> {
			// NO-OP
		}

		private interface TransformerAction extends Predicate<ClassNode> {
			// NO-OP
		}

		private static TransformerAction forMethod(MethodSignature sig, MethodAction... actions) {
			return new MethodTransformerAction(sig, actions);
		}

		private static class MethodTransformerAction implements TransformerAction {
			private final MethodSignature sig;
			private final MethodAction[] actions;

			public MethodTransformerAction(MethodSignature sig, MethodAction[] actions) {
				this.sig = sig;
				this.actions = actions;
			}

			@Override
			public boolean test(ClassNode classNode) {
				boolean didAnything = false;
				log("Applying Transformation to method (" + sig + ")");
				for (MethodAction action : actions)
					didAnything |= findMethodAndTransform(classNode, sig, action, true);
				return didAnything;
			}
		}
	
}
