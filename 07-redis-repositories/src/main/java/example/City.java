package example;

import org.springframework.data.annotation.Id;
import org.springframework.data.keyvalue.annotation.KeySpace;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
class City {
	
	String name;
	
	public City(String name) {
		this.name = name;
	}
	
}
