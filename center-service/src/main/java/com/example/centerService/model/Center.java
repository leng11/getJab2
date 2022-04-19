package com.example.centerService.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Center {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Getter
	@Setter
	private String name;
	
	@Getter
	@Setter
	private String address;
	
	@Getter
	@Setter
	private String phone;
	
	@Getter
	@Setter
	private String manager;

}
