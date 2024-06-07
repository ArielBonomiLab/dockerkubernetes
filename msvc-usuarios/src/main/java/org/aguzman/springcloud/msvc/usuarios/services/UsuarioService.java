package org.aguzman.springcloud.msvc.usuarios.services;

import java.util.List;
import java.util.Optional;

import org.aguzman.springcloud.msvc.usuarios.models.entity.Usuario;

public interface UsuarioService {
	
	List<Usuario> listar();
	Optional<Usuario> buscarPorId(Long id);
	Usuario guardar(Usuario usuario);
	void eliminar(Long id);
	List<Usuario> listarPorIds(Iterable<Long> ids);
	
	Optional<Usuario> byEmail(String email);
//	boolean existePorEmail(String email);

}
