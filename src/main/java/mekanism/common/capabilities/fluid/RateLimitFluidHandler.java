package mekanism.common.capabilities.fluid;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.annotations.NonNull;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.api.fluid.IMekanismFluidHandler;
import mekanism.api.inventory.AutomationType;
import net.minecraftforge.fluids.FluidStack;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class RateLimitFluidHandler extends ItemStackMekanismFluidHandler {

    public static RateLimitFluidHandler create(IntSupplier rate, IntSupplier capacity) {
        //TODO: Validate capacity and rate are positive?
        return new RateLimitFluidHandler(rate, capacity, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrueBi, BasicFluidTank.alwaysTrue);
    }

    public static RateLimitFluidHandler create(IntSupplier rate, IntSupplier capacity, BiPredicate<@NonNull FluidStack, @NonNull AutomationType> canExtract,
          BiPredicate<@NonNull FluidStack, @NonNull AutomationType> canInsert, Predicate<@NonNull FluidStack> validator) {
        //TODO: Validate capacity and rate are positive?
        Objects.requireNonNull(canExtract, "Extraction validity check cannot be null");
        Objects.requireNonNull(canInsert, "Insertion validity check cannot be null");
        Objects.requireNonNull(validator, "Fluid validity check cannot be null");
        return new RateLimitFluidHandler(rate, capacity, canExtract, canInsert, validator);
    }

    private IExtendedFluidTank tank;

    private RateLimitFluidHandler(IntSupplier rate, IntSupplier capacity, BiPredicate<@NonNull FluidStack, @NonNull AutomationType> canExtract,
          BiPredicate<@NonNull FluidStack, @NonNull AutomationType> canInsert, Predicate<@NonNull FluidStack> isValid) {
        tank = new RateLimitFluidTank(rate, capacity, canExtract, canInsert, isValid, this);
    }

    @Override
    protected List<IExtendedFluidTank> getInitialTanks() {
        return Collections.singletonList(tank);
    }

    private static class RateLimitFluidTank extends VariableCapacityFluidTank {

        private final IntSupplier rate;

        private RateLimitFluidTank(IntSupplier rate, IntSupplier capacity, BiPredicate<@NonNull FluidStack, @NonNull AutomationType> canExtract,
              BiPredicate<@NonNull FluidStack, @NonNull AutomationType> canInsert, Predicate<@NonNull FluidStack> isValid, IMekanismFluidHandler fluidHandler) {
            super(capacity, canExtract, canInsert, isValid, fluidHandler);
            this.rate = rate;
        }

        @Override
        protected int getRate() {
            return rate.getAsInt();
        }
    }
}