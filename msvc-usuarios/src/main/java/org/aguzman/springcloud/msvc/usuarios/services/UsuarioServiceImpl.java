package org.aguzman.springcloud.msvc.usuarios.services;

import java.util.List;
import java.util.Optional;

import org.aguzman.springcloud.msvc.usuarios.clients.CursoClienteRest;
import org.aguzman.springcloud.msvc.usuarios.models.entity.Usuario;
import org.aguzman.springcloud.msvc.usuarios.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private CursoClienteRest client;
	
	@Transactional(readOnly = true)
	@Override
	public List<Usuario> listar() {
		return (List<Usuario>) usuarioRepository.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<Usuario> buscarPorId(Long id) {
		return usuarioRepository.findById(id);
	}

	@Transactional
	@Override
	public Usuario guardar(Usuario usuario) {
		return usuarioRepository.save(usuario);
	}

	@Transactional
	@Override
	public void eliminar(Long id) {
		usuarioRepository.deleteById(id);
		client.eliminarCursoUsuarioPorId(id);
	}

	
	
	@Override
	public Optional<Usuario> byEmail(String email) {
		return usuarioRepository.findByEmail(email);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Usuario> listarPorIds(Iterable<Long> ids) {
		return (List<Usuario>) usuarioRepository.findAllById(ids);
	}

	/*
	 * //Este metodo hace lo mismo que el anterior solo que devuelve un Booleano.
	 * 
	 * @Override public boolean existePorEmail(String email) { return
	 * usuarioRepository.existByEmail(email); }
	 */
	
}
