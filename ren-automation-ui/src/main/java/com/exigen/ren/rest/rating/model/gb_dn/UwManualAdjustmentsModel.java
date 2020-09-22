package com.exigen.ren.rest.rating.model.gb_dn;

import com.exigen.ipb.eisa.ws.rest.model.Model;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UwManualAdjustmentsModel extends Model {

    private String seqNo;
    private String percentage;
    private String reasonCd;
    private String comment;

    public String getSeqNo() {
        return seqNo;
    }

    public UwManualAdjustmentsModel setSeqNo(String seqNo) {
        this.seqNo = seqNo;
        return this;
    }

    public String getPercentage() {
        return percentage;
    }

    public UwManualAdjustmentsModel setPercentage(String percentage) {
        this.percentage = percentage;
        return this;
    }

    public String getReasonCd() {
        return reasonCd;
    }

    public UwManualAdjustmentsModel setReasonCd(String reasonCd) {
        this.reasonCd = reasonCd;
        return this;
    }

    public String getComment() {
        return comment;
    }

    public UwManualAdjustmentsModel setComment(String comment) {
        this.comment = comment;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UwManualAdjustmentsModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        UwManualAdjustmentsModel that = (UwManualAdjustmentsModel) o;
        return Objects.equals(seqNo, that.seqNo) &&
                Objects.equals(percentage, that.percentage) &&
                Objects.equals(reasonCd, that.reasonCd) &&
                Objects.equals(comment, that.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), seqNo, percentage, reasonCd, comment);
    }

    @Override
    public String toString() {
        return "UwManualAdjustmentsModel{" +
                "seqNo='" + seqNo + '\'' +
                ", percentage='" + percentage + '\'' +
                ", reasonCd='" + reasonCd + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}