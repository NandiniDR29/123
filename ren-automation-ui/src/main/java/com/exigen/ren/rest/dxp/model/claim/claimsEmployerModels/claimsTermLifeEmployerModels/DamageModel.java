package com.exigen.ren.rest.dxp.model.claim.claimsEmployerModels.claimsTermLifeEmployerModels;

import com.exigen.ren.rest.model.RestError;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DamageModel extends RestError {
    private DeathModel death;
    private PremiumWaiverModel premiumWaiver;
    private AcceleratedDeathModel acceleratedDeath;
    private AccidentalDismembermentModel accidentalDismemberment;
    private AccidentalDeathModel accidentalDeath;

    public DeathModel getDeath() {
        return death;
    }

    public void setDeath(DeathModel death) {
        this.death = death;
    }

    public PremiumWaiverModel getPremiumWaiver() {
        return premiumWaiver;
    }

    public void setPremiumWaiver(PremiumWaiverModel premiumWaiver) {
        this.premiumWaiver = premiumWaiver;
    }

    public AcceleratedDeathModel getAcceleratedDeath() {
        return acceleratedDeath;
    }

    public void setAcceleratedDeath(AcceleratedDeathModel acceleratedDeath) {
        this.acceleratedDeath = acceleratedDeath;
    }

    public AccidentalDismembermentModel getAccidentalDismemberment() {
        return accidentalDismemberment;
    }

    public void setAccidentalDismemberment(AccidentalDismembermentModel accidentalDismemberment) {
        this.accidentalDismemberment = accidentalDismemberment;
    }

    public AccidentalDeathModel getAccidentalDeath() {
        return accidentalDeath;
    }

    public void setAccidentalDeath(AccidentalDeathModel accidentalDeath) {
        this.accidentalDeath = accidentalDeath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        DamageModel that = (DamageModel) o;
        return Objects.equals(death, that.death) &&
                Objects.equals(premiumWaiver, that.premiumWaiver) &&
                Objects.equals(acceleratedDeath, that.acceleratedDeath) &&
                Objects.equals(accidentalDismemberment, that.accidentalDismemberment) &&
                Objects.equals(accidentalDeath, that.accidentalDeath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), death, premiumWaiver, acceleratedDeath, accidentalDismemberment, accidentalDeath);
    }
}
