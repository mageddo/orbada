package pl.mpak.util.patt;

public class DefinedModel implements ResolvableModel {

  private String model;
  
  public DefinedModel(String model) {
    this.model = model;
  }
  
  public String getModel() {
    return model;
  }

  public String getResolve() {
    return System.getProperty(model);
  }

}
