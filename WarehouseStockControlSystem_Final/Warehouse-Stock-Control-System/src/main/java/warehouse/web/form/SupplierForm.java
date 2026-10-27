package warehouse.web.form;

import jakarta.validation.constraints.NotBlank;
import warehouse.model.Supplier;

public class SupplierForm {
    @NotBlank(message = "Supplier name is required.") private String name;
    @NotBlank(message = "Contact information is required.") private String contactInfo;
    public static SupplierForm from(Supplier s) { SupplierForm f=new SupplierForm(); f.name=s.getName(); f.contactInfo=s.getContactInfo(); return f; }
    public Supplier toSupplier() { return new Supplier(name, contactInfo); }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getContactInfo() { return contactInfo; } public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }
}
