package ua.pp.jdev.permits.data.cassandra;

import org.springframework.data.cassandra.core.mapping.UserDefinedType;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@UserDefinedType("xpermit")
class XPermitUDT {
	@NonNull
	private String xpermit;
}
