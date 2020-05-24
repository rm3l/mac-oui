package org.rm3l.macoui.services.data;

import java.util.Objects;
import java.util.Set;
import javax.json.bind.annotation.JsonbNillable;
import javax.json.bind.annotation.JsonbProperty;

@JsonbNillable
public class MacOui {

  private String manufacturer;

  @JsonbProperty("address")
  private Set<String> addresses;

  private String prefix;

  private String country;

  private String comment;

  public String getManufacturer() {
    return manufacturer;
  }

  public MacOui setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public Set<String> getAddresses() {
    return addresses;
  }

  public MacOui setAddresses(Set<String> addresses) {
    this.addresses = addresses;
    return this;
  }

  public String getPrefix() {
    return prefix;
  }

  public MacOui setPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  public String getCountry() {
    return country;
  }

  public MacOui setCountry(String country) {
    this.country = country;
    return this;
  }

  public String getComment() {
    return comment;
  }

  public MacOui setComment(String comment) {
    this.comment = comment;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MacOui macOui = (MacOui) o;
    return Objects.equals(prefix, macOui.prefix);
  }

  @Override
  public int hashCode() {
    return Objects.hash(prefix);
  }
}
