package pl.mpak.orbada.reports.bean;

import java.io.Serializable;

import java.sql.Timestamp;
import pl.mpak.usedb.ann.Column;
import pl.mpak.usedb.ann.Table;
import pl.mpak.util.id.UniqueID;
import pl.mpak.util.variant.VariantType;

@Table(name="OREP_GROUPS", primaryKey={"OREPG_ID"})
public class ReportsGroup implements Serializable {
  private static final long serialVersionUID = -7233768155657132700L;

  private String id;
  private Long created;
  private Long updated;
  private String usrId;
  private String dtpId;
  private String schId;
  private String orepgId;
  private String shared;
  private String name;
  private String tooltip;
  private String description;
  
  public ReportsGroup() {
    super();
    id = new UniqueID().toString();
  }
  
  public ReportsGroup(String id, String dtpId, String orepgId, String name, String tooltip, String description) {
    this();
    setId(id);
    setDtpId(dtpId);
    setOrepgId(orepgId);
    setName(name);
    setTooltip(tooltip);
    setDescription(description);
  }

  @Column(name="OREPG_ID", type=VariantType.varString, updatable=false)
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Column(name="OREPG_CREATED", type=VariantType.varLong)
  public Long getCreated() {
    return created;
  }

  public void setCreated(Long created) {
    this.created = created;
  }

  @Column(name="OREPG_UPDATED", type=VariantType.varLong)
  public Long getUpdated() {
    return updated;
  }

  public void setUpdated(Long updated) {
    this.updated = updated;
  }

  @Column(name="OREPG_USR_ID", type=VariantType.varString)
  public String getUsrId() {
    return usrId;
  }

  public void setUsrId(String usrId) {
    this.usrId = usrId;
  }

  @Column(name="OREPG_DTP_ID", type=VariantType.varString)
  public String getDtpId() {
    return dtpId;
  }

  public void setDtpId(String dtpId) {
    this.dtpId = dtpId;
  }

  @Column(name="OREPG_SCH_ID", type=VariantType.varString)
  public String getSchId() {
    return schId;
  }

  public void setSchId(String schId) {
    this.schId = schId;
  }

  @Column(name="OREPG_OREPG_ID", type=VariantType.varString)
  public String getOrepgId() {
    return orepgId;
  }

  public void setOrepgId(String orepgId) {
    this.orepgId = orepgId;
  }

  @Column(name="OREPG_SHARED", type=VariantType.varString)
  public String getShared() {
    return shared;
  }

  public void setShared(String shared) {
    this.shared = shared;
  }

  @Column(name="OREPG_NAME", type=VariantType.varString)
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Column(name="OREPG_TOOLTIP", type=VariantType.varString)
  public String getTooltip() {
    return tooltip;
  }

  public void setTooltip(String tooltip) {
    this.tooltip = tooltip;
  }

  @Column(name="OREPG_DESCRIPTION", type=VariantType.varString)
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }



}
