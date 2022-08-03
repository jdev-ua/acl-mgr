package ua.pp.jdev.permits.data.orm.cassandra;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@UserDefinedType("obj_type")
class ObjTypeUDT {
	@NonNull
	private String objType;
}
