package com.ss.design8or.model;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author ezerbo
 *
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"assignments", "designations"})
@EqualsAndHashCode(exclude = {"assignments", "designations"})
@Table(name = "user", catalog = "desig8or_db",
uniqueConstraints = { @UniqueConstraint(columnNames = "email_address") })
public class User implements Serializable {

	private static final long serialVersionUID = 7758127706202666953L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private Long id;
	
	@NotBlank
	@Column(name = "first_name", nullable = false)
	private String firstName;

	@NotBlank
	@Column(name = "last_name", nullable = false)
	private String lastName;
	
	@Email
	@Column(name = "email_address", unique = true, nullable = false)
	private String emailAddress;
	
	@Column(name = "is_lead")
	public boolean lead; //TODO use assignment status to determine lead.
	
	@JsonIgnore
	@OneToMany(mappedBy = "user", orphanRemoval = true)
	private List<Assignment> assignments;
	
	@JsonIgnore
	@OneToMany(mappedBy = "user", orphanRemoval = true)
	private List<Designation> designations;
	
	public User lead(boolean lead) {
		setLead(lead);
		return this;
	}
}