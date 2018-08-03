package com.chocohead.nottmi.loader;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.inventory.Container;
import net.minecraft.launchwrapper.IClassTransformer;

import com.chocohead.nottmi.NotTMI;
import com.chocohead.nottmi.NotTMILog;
import com.chocohead.nottmi.Util;

public class NotTMITransformer implements IClassTransformer {
	private static final String Minecraft = Util.inDev() ? "net.minecraft.client.Minecraft" : "cfi";
	private static final String MinecraftOwner = Minecraft.replace('.', '/');
	private static final String currentScreen = Util.inDev() ? "currentScreen" : "m";
	private static final String GuiScreen = Util.inDev() ? "net.minecraft.client.gui.GuiScreen" : "cjs";
	private static final String GuiScreenOwner = GuiScreen.replace('.', '/');
	private static final String displayGuiScreen = Util.inDev() ? "displayGuiScreen" : "a";
	private static final String GuiContainer = Util.inDev() ? "net.minecraft.client.gui.inventory.GuiContainer" : "ckn";
	private static final String GuiContainerOwner = GuiContainer.replace('.', '/');
	private static final String xSize = Util.inDev() ? "xSize" : "f";
	private static final String ySize = Util.inDev() ? "ySize" : "g";
	private static final String guiLeft = Util.inDev() ? "guiLeft" : "i";
	private static final String guiTop = Util.inDev() ? "guiTop" : "s";
	private static final String initGui = Util.inDev() ? "initGui" : "c";
	private static final String drawScreen = Util.inDev() ? "drawScreen" : "a";
	private static final String drawGuiContainerForegroundLayer = Util.inDev() ? "drawGuiContainerForegroundLayer" : "c";
	private static final String keyPressed = "keyPressed";
	private static final String onGuiClosed = Util.inDev() ? "onGuiClosed" : "n";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (Minecraft.equals(name)) {
			ClassNode node = new ClassNode();
			new ClassReader(basicClass).accept(node, 0);

			for (MethodNode method : node.methods) {
				if (displayGuiScreen.equals(method.name) && ("(L" + GuiScreenOwner + ";)V").equals(method.desc)) {
					boolean injection = false;

					for (Iterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();) {
						AbstractInsnNode instruction = it.next();

						if (instruction.getType() == AbstractInsnNode.FIELD_INSN && instruction.getOpcode() == Opcodes.PUTFIELD) {
							FieldInsnNode f = (FieldInsnNode) instruction;

							if (MinecraftOwner.equals(f.owner) && currentScreen.equals(f.name) && ('L' + GuiScreenOwner + ';').equals(f.desc)) {
								InsnList l = new InsnList();

								l.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/chocohead/nottmi/NotTMI", "INSTANCE", "Lcom/chocohead/nottmi/NotTMI;"));
								l.add(new VarInsnNode(Opcodes.ALOAD, 1));
								l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/chocohead/nottmi/NotTMI", "bind", "(L" + GuiScreenOwner + ";)V", false));

								method.instructions.insertBefore(instruction.getPrevious().getPrevious(), l);
								injection = true;
								break;
							}
						}
					}

					NotTMILog.info("Injection for displayGuiScreen: " + injection);
					break;
				}
			}

			ClassWriter writer = new ClassWriter(0);
			node.accept(writer);
			return writer.toByteArray();
		} else if (GuiContainer.equals(name)) {
			ClassNode node = new ClassNode();
			new ClassReader(basicClass).accept(node, 0);

			for (MethodNode method : node.methods) {
				NotTMILog.info("Passed " + method.name);
				if (initGui.equals(method.name) && "()V".equals(method.desc)) {
					boolean injection = false;

					for (Iterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();) {
						AbstractInsnNode instruction = it.next();

						if (instruction.getType() == AbstractInsnNode.INSN && instruction.getOpcode() == Opcodes.RETURN) {
							InsnList l = new InsnList();

							l.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/chocohead/nottmi/NotTMI", "INSTANCE", "Lcom/chocohead/nottmi/NotTMI;"));
							l.add(new VarInsnNode(Opcodes.ALOAD, 0));
							l.add(new FieldInsnNode(Opcodes.GETFIELD, GuiContainerOwner, xSize, "I"));
							l.add(new VarInsnNode(Opcodes.ALOAD, 0));
							l.add(new FieldInsnNode(Opcodes.GETFIELD, GuiContainerOwner, ySize, "I"));
							l.add(new VarInsnNode(Opcodes.ALOAD, 0));
							l.add(new FieldInsnNode(Opcodes.GETFIELD, GuiContainerOwner, guiLeft, "I"));
							l.add(new VarInsnNode(Opcodes.ALOAD, 0));
							l.add(new FieldInsnNode(Opcodes.GETFIELD, GuiContainerOwner, guiTop, "I"));
							l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/chocohead/nottmi/NotTMI", "setSize", "(IIII)V", false));

							method.instructions.insertBefore(instruction, l);
							method.maxStack = 5;
							injection = true;
							break;
						}
					}

					NotTMILog.info("Injection for initGui: " + injection);
				} else if (drawScreen.equals(method.name) && "(IIF)V".equals(method.desc)) {
					boolean injection = false;

					for (Iterator<AbstractInsnNode> it = method.instructions.iterator(); it.hasNext();) {
						AbstractInsnNode instruction = it.next();

						if (instruction.getType() == AbstractInsnNode.METHOD_INSN && instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
							MethodInsnNode m = (MethodInsnNode) instruction;

							//NotTMILog.info("Virtual invoke: " + m.owner + '#' + m.name + m.desc);
							if (GuiContainerOwner.equals(m.owner) && drawGuiContainerForegroundLayer.equals(m.name) && "(II)V".equals(m.desc)) {
								InsnList l = new InsnList();

								l.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/chocohead/nottmi/NotTMI", "INSTANCE", "Lcom/chocohead/nottmi/NotTMI;"));
								l.add(new VarInsnNode(Opcodes.ILOAD, 1));
								l.add(new VarInsnNode(Opcodes.ILOAD, 2));
								l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/chocohead/nottmi/NotTMI", "onDraw", "(II)V", false));

								method.instructions.insertBefore(m.getPrevious().getPrevious().getPrevious(), l);
								injection = true;
								break;
							}
						}
					}

					NotTMILog.info("Injection for drawScreen: " + injection);
				} else if (keyPressed.equals(method.name) && "(III)Z".equals(method.desc)) {
					InsnList l = new InsnList();

					l.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/chocohead/nottmi/NotTMI", "INSTANCE", "Lcom/chocohead/nottmi/NotTMI;"));
					l.add(new VarInsnNode(Opcodes.ILOAD, 1));
					l.add(new VarInsnNode(Opcodes.ILOAD, 2));
					l.add(new VarInsnNode(Opcodes.ILOAD, 3));
					l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/chocohead/nottmi/NotTMI", "onKey", "(III)Z", false));
					LabelNode notHandled = new LabelNode();
					l.add(new JumpInsnNode(Opcodes.IFEQ, notHandled));
					l.add(new InsnNode(Opcodes.ICONST_1));
					l.add(new InsnNode(Opcodes.IRETURN));
					l.add(notHandled);
					l.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));

					method.instructions.insert(method.instructions.getFirst(), l);

					NotTMILog.info("Injection for keyPressed: true");
				} else if (onGuiClosed.equals(method.name) && "()V".equals(method.desc)) {
					InsnList l = new InsnList();

					l.add(new FieldInsnNode(Opcodes.GETSTATIC, "com/chocohead/nottmi/NotTMI", "INSTANCE", "Lcom/chocohead/nottmi/NotTMI;"));
					l.add(new VarInsnNode(Opcodes.ALOAD, 0));
					l.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "com/chocohead/nottmi/NotTMI", "release", "(L" + GuiContainerOwner + ";)V", false));

					method.instructions.insert(method.instructions.getFirst(), l);

					NotTMILog.info("Injection for onGuiClosed: true");
				}
			}

			ClassWriter writer = new ClassWriter(0);
			node.accept(writer);
			return writer.toByteArray();
		} else {
			return basicClass;
		}
	}

	abstract class Test extends net.minecraft.client.gui.inventory.GuiContainer {
		public Test(Container inventorySlotsIn)
		{
			super(inventorySlotsIn);
			NotTMI.INSTANCE.bind(this);
		}

		@Override
		public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
			if (NotTMI.INSTANCE.onKey(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
				return true;
			}
			return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
		}
	}
}