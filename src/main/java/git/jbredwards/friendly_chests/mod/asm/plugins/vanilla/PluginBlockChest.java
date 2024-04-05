package git.jbredwards.friendly_chests.mod.asm.plugins.vanilla;

import git.jbredwards.friendly_chests.api.asm.IASMPlugin;
import git.jbredwards.friendly_chests.api.ChestType;
import git.jbredwards.friendly_chests.api.IChestMatchable;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author jbred
 *
 */
public final class PluginBlockChest implements IASMPlugin
{
    @Override
    public boolean isMethodValid(@Nonnull MethodNode method, boolean obfuscated) { return method.name.equals(obfuscated ? "func_189540_a" : "neighborChanged"); }

    @Override
    public boolean transform(@Nonnull InsnList instructions, @Nonnull MethodNode method, @Nonnull AbstractInsnNode insn, boolean obfuscated, int index) {
        /*
         * neighborChanged: (changes are around line 430)
         * Old code:
         * super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
         *
         * New code:
         * //update state here when a neighbor disappears
         * super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
         * Hooks.neighborChanged(this, state, worldIn, pos);
         */
        if(insn.getOpcode() == INVOKESPECIAL) {
            final InsnList list = new InsnList();
            list.add(new VarInsnNode(ALOAD, 0));
            list.add(new VarInsnNode(ALOAD, 1));
            list.add(new VarInsnNode(ALOAD, 2));
            list.add(new VarInsnNode(ALOAD, 3));
            list.add(genMethodNode("neighborChanged", "(Lnet/minecraft/block/BlockChest;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"));
            instructions.insert(insn, list);
            return true;
        }

        return false;
    }

    @Override
    public boolean transformClass(@Nonnull ClassNode classNode, boolean obfuscated) {
        classNode.interfaces.add("git/jbredwards/friendly_chests/api/IChestMatchable");
        classNode.methods.removeIf(method -> method.name.equals(obfuscated ? "func_176196_c" : "canPlaceBlockAt"));
        /*
         * getBoundingBox:
         * New code:
         * public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
         * {
         *     return Hooks.getBoundingBox(state);
         * }
         */
        overrideMethod(classNode, method -> method.name.equals(obfuscated ? "func_185496_a" : "getBoundingBox"),
            "getBoundingBox", "(Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/util/math/AxisAlignedBB;",
                generator -> generator.visitVarInsn(ALOAD, 1));
        /*
         * onBlockAdded:
         * New code:
         * public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
         * {
         *     Hooks.onBlockAdded(this, worldIn, pos, state);
         * }
         */
        overrideMethod(classNode, method -> method.name.equals(obfuscated ? "func_176213_c" : "onBlockAdded"),
            "onBlockAdded", "(Lnet/minecraft/block/BlockChest;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)V", generator -> {
                generator.visitVarInsn(ALOAD, 0);
                generator.visitVarInsn(ALOAD, 1);
                generator.visitVarInsn(ALOAD, 2);
                generator.visitVarInsn(ALOAD, 3);
            }
        );
        /*
         * getStateForPlacement:
         * New code:
         * public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
         * {
         *     return Hooks.getStateForPlacement(this, world, pos, facing, placer);
         * }
         */
        overrideMethod(classNode, method -> method.name.equals(obfuscated ? "func_180642_a" : "getStateForPlacement"),
            "getStateForPlacement", "(Lnet/minecraft/block/BlockChest;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/block/state/IBlockState;", generator -> {
                generator.visitVarInsn(ALOAD, 0);
                generator.visitVarInsn(ALOAD, 1);
                generator.visitVarInsn(ALOAD, 2);
                generator.visitVarInsn(ALOAD, 3);
                generator.visitVarInsn(ALOAD, 8);
            }
        );
        /*
         * onBlockPlacedBy:
         * New code:
         * public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
         * {
         *     Hooks.onBlockPlacedBy(worldIn, pos, state, stack);
         * }
         */
        overrideMethod(classNode, method -> method.name.equals(obfuscated ? "func_180633_a" : "onBlockPlacedBy"),
            "onBlockPlacedBy", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/item/ItemStack;)V", generator -> {
                generator.visitVarInsn(ALOAD, 1);
                generator.visitVarInsn(ALOAD, 2);
                generator.visitVarInsn(ALOAD, 3);
                generator.visitVarInsn(ALOAD, 5);
            }
        );
        /*
         * getContainer:
         * New code:
         * @Nullable
         * public ILockableContainer getContainer(World worldIn, BlockPos pos, boolean allowBlocking)
         * {
         *     return Hooks.getContainer(this, worldIn, pos, allowBlocking);
         * }
         */
        overrideMethod(classNode, method -> method.name.equals(obfuscated? "func_189418_a" : "getContainer"),
            "getContainer", "(Lnet/minecraft/block/BlockChest;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Z)Lnet/minecraft/world/ILockableContainer;", generator -> {
                generator.visitVarInsn(ALOAD, 0);
                generator.visitVarInsn(ALOAD, 1);
                generator.visitVarInsn(ALOAD, 2);
                generator.visitVarInsn(ILOAD, 3);
            }
        );
        /*
         * getStateFromMeta:
         * New code:
         * public IBlockState getStateFromMeta(int meta)
         * {
         *     return Hooks.getStateFromMeta(this, meta);
         * }
         */
        overrideMethod(classNode, method -> method.name.equals(obfuscated? "func_176203_a" : "getStateFromMeta"),
            "getStateFromMeta", "(Lnet/minecraft/block/BlockChest;I)Lnet/minecraft/block/state/IBlockState;", generator -> {
                generator.visitVarInsn(ALOAD, 0);
                generator.visitVarInsn(ILOAD, 1);
            }
        );
        /*
         * getMetaFromState:
         * New code:
         * public int getMetaFromState(IBlockState state)
         * {
         *     return Hooks.getMetaFromState(state);
         * }
         */
        overrideMethod(classNode, method -> method.name.equals(obfuscated? "func_176201_c" : "getMetaFromState"),
            "getMetaFromState", "(Lnet/minecraft/block/state/IBlockState;)I",
                generator -> generator.visitVarInsn(ALOAD, 1));
        /*
         * createBlockState:
         * New code:
         * public BlockStateContainer createBlockState()
         * {
         *     return Hooks.createBlockState(this);
         * }
         */
        overrideMethod(classNode, method -> method.name.equals(obfuscated? "func_180661_e" : "createBlockState"),
            "createBlockState", "(Lnet/minecraft/block/BlockChest;)Lnet/minecraft/block/state/BlockStateContainer;",
                generator -> generator.visitVarInsn(ALOAD, 0));
        /*
         * onNeighborChange:
         * New code:
         * public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
         * {
         *     Hooks.onNeighborChange(world, pos, neighbor);
         * }
         */
        addMethod(classNode, "onNeighborChange", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)V",
            "onNeighborChange", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;)V", generator -> {
                generator.visitVarInsn(ALOAD, 1);
                generator.visitVarInsn(ALOAD, 2);
                generator.visitVarInsn(ALOAD, 3);
            }
        );

        return true;
    }

    @SuppressWarnings("unused")
    public static final class Hooks
    {
        @Nonnull
        public static BlockStateContainer createBlockState(@Nonnull BlockChest block) {
            return new BlockStateContainer.Builder(block).add(BlockChest.FACING, ChestType.TYPE).build();
        }

        @Nonnull
        public static AxisAlignedBB getBoundingBox(@Nonnull IBlockState state) {
            if(state.getValue(ChestType.TYPE) == ChestType.SINGLE) return BlockChest.NOT_CONNECTED_AABB;
            switch(ChestType.getDirectionToAttached(state)) {
                case NORTH: return BlockChest.NORTH_CHEST_AABB;
                case SOUTH: return BlockChest.SOUTH_CHEST_AABB;
                case WEST: return BlockChest.WEST_CHEST_AABB;
                case EAST: return BlockChest.EAST_CHEST_AABB;
            }

            //should never pass
            return BlockChest.NOT_CONNECTED_AABB;
        }

        @Nullable
        public static ILockableContainer getContainer(@Nonnull BlockChest block, @Nonnull World world, @Nonnull BlockPos pos, boolean allowBlocking) {
            final TileEntity tile = world.getTileEntity(pos);
            if(!(tile instanceof TileEntityChest) || !allowBlocking && block.isBlocked(world, pos))
                return null;

            ILockableContainer container = (ILockableContainer)tile;
            final IBlockState state = world.getBlockState(pos);

            final ChestType type = state.getValue(ChestType.TYPE);
            if(type == ChestType.SINGLE) return container;

            final BlockPos neighborPos = pos.offset(type.rotate(state.getValue(BlockChest.FACING)));
            if(!allowBlocking && block.isBlocked(world, neighborPos)) return null;

            final ILockableContainer neighbor = (ILockableContainer)world.getTileEntity(neighborPos);
            if(neighbor == null) return null;

            return new InventoryLargeChest("container.chestDouble",
                    type == ChestType.RIGHT ? neighbor : container,
                    type == ChestType.LEFT ? neighbor : container);
        }

        public static int getMetaFromState(@Nonnull IBlockState state) {
            return state.getValue(BlockChest.FACING).getHorizontalIndex()
                    | state.getValue(ChestType.TYPE).ordinal() << 2;
        }

        @Nonnull
        public static IBlockState getStateFromMeta(@Nonnull BlockChest block, int meta) {
            return block.getDefaultState()
                    .withProperty(BlockChest.FACING, EnumFacing.byHorizontalIndex(meta & 3))
                    .withProperty(ChestType.TYPE, ChestType.fromIndex(meta >> 2));
        }

        @Nonnull
        public static IBlockState getStateForPlacement(@Nonnull BlockChest block, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side, @Nonnull EntityLivingBase placer) {
            final boolean isSneaking = placer.isSneaking();
            EnumFacing facing = placer.getHorizontalFacing().getOpposite();
            ChestType type = ChestType.SINGLE;

            if(side.getAxis().isHorizontal() && isSneaking) {
                final EnumFacing sideToAttach = getDirectionToAttach(block, world, pos, side.getOpposite());
                if(sideToAttach != null && sideToAttach.getAxis() != side.getAxis()) {
                    facing = sideToAttach;
                    type = sideToAttach.rotateYCCW() == side.getOpposite() ? ChestType.RIGHT : ChestType.LEFT;
                }
            }

            if(type == ChestType.SINGLE && !isSneaking) {
                final EnumFacing left = getDirectionToAttach(block, world, pos, facing.rotateY());
                if(left != null && facing != left.getOpposite()) type = ChestType.LEFT;
                else {
                    final EnumFacing right = getDirectionToAttach(block, world, pos, facing.rotateYCCW());
                    if(right != null && facing != right.getOpposite()) type = ChestType.RIGHT;
                }
            }

            return block.getDefaultState().withProperty(BlockChest.FACING, facing).withProperty(ChestType.TYPE, type);
        }

        public static void neighborChanged(@Nonnull BlockChest block, @Nonnull IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
            if(state.getValue(ChestType.TYPE) != ChestType.SINGLE) {
                final BlockPos otherPos = pos.offset(ChestType.getDirectionToAttached(state));
                final IBlockState other = world.getBlockState(otherPos);
                if(!IChestMatchable.chestMatches(block, world, state, pos, other, otherPos))
                    world.setBlockState(pos, state.withProperty(ChestType.TYPE, ChestType.SINGLE));
            }
        }

        public static void onBlockAdded(@Nonnull BlockChest block, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
            final ChestType type = state.getValue(ChestType.TYPE);
            if(type != ChestType.SINGLE) {
                final EnumFacing facing = state.getValue(BlockChest.FACING);
                final BlockPos offset = pos.offset(type.rotate(facing));
                final IBlockState attachedTo = world.getBlockState(offset);

                //update neighbor chest
                if(IChestMatchable.chestMatches(block, world, state, pos, attachedTo, offset)
                && attachedTo.getValue(ChestType.TYPE) == ChestType.SINGLE)
                    world.setBlockState(offset, attachedTo
                            .withProperty(BlockChest.FACING, facing)
                            .withProperty(ChestType.TYPE, type.getOpposite()));

                //update this chest
                else world.setBlockState(pos, state.withProperty(ChestType.TYPE, ChestType.SINGLE));
            }
        }

        public static void onBlockPlacedBy(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull ItemStack stack) {
            if(world.isRemote) state.getBlock().onBlockAdded(world, pos, state); //sync client early cause rendering glitch
            if(stack.hasDisplayName()) {
                final @Nullable TileEntity tile = world.getTileEntity(pos);
                if(tile instanceof TileEntityChest) ((TileEntityChest)tile).setCustomName(stack.getDisplayName());
            }
        }

        public static void onNeighborChange(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, @Nonnull BlockPos neighbor) {
            if(world instanceof World && ((World)world).isRemote) {
                final TileEntity tile = world.getTileEntity(pos);
                if(tile instanceof TileEntityChest) {
                    if(pos.offset(EnumFacing.NORTH).equals(neighbor)) ((TileEntityChest)tile).adjacentChestZNeg = null;
                    if(pos.offset(EnumFacing.WEST).equals(neighbor)) ((TileEntityChest)tile).adjacentChestXNeg = null;
                }
            }
        }

        //helper
        @Nullable
        public static EnumFacing getDirectionToAttach(@Nonnull BlockChest block, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing) {
            final IBlockState state = world.getBlockState(pos.offset(facing));
            return IChestMatchable.chestMatches(block, world, block.getDefaultState(), pos, state, pos.offset(facing))
                    && state.getValue(ChestType.TYPE) == ChestType.SINGLE ? state.getValue(BlockChest.FACING) : null;
        }
    }
}
