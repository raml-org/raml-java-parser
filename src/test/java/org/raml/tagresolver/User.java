/*
 * Copyright 2016 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.tagresolver;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(namespace = "http://raml.org/schemas/test")
@XmlType(namespace = "http://raml.org/schemas/test", propOrder = {"emailAddresses"})
@JsonAutoDetect
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class User implements Serializable
{

    private String username;
    private String firstName;
    private String lastName;
    private List<String> emailAddresses;

    @XmlAttribute(required = true)
    @JsonProperty(required = true)
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @XmlAttribute(required = true)
    @JsonProperty(required = true)
    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    @XmlAttribute(required = true)
    @JsonProperty(required = true)
    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    @XmlElementWrapper(required = true, name = "email-addresses", namespace = "http://raml.org/schemas/test")
    @XmlElement(required = true, name = "email-address", namespace = "http://raml.org/schemas/test", type = String.class)
    @JsonProperty(required = true)
    public List<String> getEmailAddresses()
    {
        return emailAddresses;
    }

    public void setEmailAddresses(List<String> emailAddresses)
    {
        this.emailAddresses = emailAddresses;
    }

}
