// The MIT License (MIT)
//
// Copyright (c) 2020 Armel Soro
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
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
