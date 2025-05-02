package com.ss.design8or.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

/**
 * @author ezerbo
 *
 */
@Data
@Entity
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"assignments", "designations"})
@EqualsAndHashCode(exclude = {"assignments", "designations"})
@Table(name = "user", catalog = "design8or_db")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public User elect() {
		setLead(true);
		return this;
	}
	
	public User unElect() {
		setLead(false);
		return this;
	}
}