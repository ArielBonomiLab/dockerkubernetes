package org.bonomia.springcloud.msvc.cursos.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bonomia.springcloud.msvc.cursos.clients.UsuarioClientRest;
import org.bonomia.springcloud.msvc.cursos.models.Usuario;
import org.bonomia.springcloud.msvc.cursos.models.entity.Curso;
import org.bonomia.springcloud.msvc.cursos.models.entity.CursoUsuario;
import org.bonomia.springcloud.msvc.cursos.repositories.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CursoServiceImpl implements CursoService {

	@Autowired
	private CursoRepository cursoRepository;

	@Autowired
	private UsuarioClientRest client;

	@Transactional(readOnly = true)
	@Override
	public List<Curso> listar() {
		return (List<Curso>) cursoRepository.findAll();
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<Curso> porId(Long id) {
		return cursoRepository.findById(id);
	}

	@Transactional
	@Override
	public Curso guardar(Curso curso) {
		return cursoRepository.save(curso);
	}

	@Transactional
	@Override
	public void eliminar(Long id) {
		cursoRepository.deleteById(id);
	}

	@Transactional
	@Override
	public Optional<Usuario> asignarUsuario(Usuario usuario, Long cursoId) {
		Optional<Curso> o = cursoRepository.findById(cursoId);
		if (o.isPresent()) {
			Usuario usuarioMsvc = client.getUsuario(usuario.getId());
			Curso curso = o.get();
			CursoUsuario cursoUsuario = new CursoUsuario();
			cursoUsuario.setUsuarioId(usuarioMsvc.getId());
			curso.addCursoUsuario(cursoUsuario);
			cursoRepository.save(curso);
			return Optional.of(usuarioMsvc);
		}
		return Optional.empty();
	}

	@Transactional
	@Override
	public Optional<Usuario> crearUsuario(Usuario usuario, Long cursoId) {
		Optional<Curso> o = cursoRepository.findById(cursoId);
		if (o.isPresent()) {
			Usuario usuarioNuevoMsvc = client.crearUsuario(usuario);
			Curso curso = o.get();
			CursoUsuario cursoUsuario = new CursoUsuario();
			cursoUsuario.setUsuarioId(usuarioNuevoMsvc.getId());
			curso.addCursoUsuario(cursoUsuario);
			cursoRepository.save(curso);
			return Optional.of(usuarioNuevoMsvc);
		}
		return Optional.empty();
	}

	@Transactional
	@Override
	public Optional<Usuario> desasignarUsuario(Usuario usuario, Long cursoId) {
		Optional<Curso> o = cursoRepository.findById(cursoId);
		if (o.isPresent()) {
			Usuario usuarioMsvc = client.getUsuario(usuario.getId());
			Curso curso = o.get();
			CursoUsuario cursoUsuario = new CursoUsuario();
			cursoUsuario.setUsuarioId(usuarioMsvc.getId());
			curso.removeCursoUsuario(cursoUsuario);
			cursoRepository.save(curso);
			return Optional.of(usuarioMsvc);
		}
		return Optional.empty();
	}

	@Transactional
	@Override
	public Optional<Usuario> eliminarUsuario(Usuario usuario, Long cursoId) {
		Optional<Curso> o = cursoRepository.findById(cursoId);
		if (o.isPresent()) {
			Usuario usuarioMsvc = client.getUsuario(usuario.getId());
			Curso curso = o.get();
			CursoUsuario cursoUsuario = new CursoUsuario();
			cursoUsuario.setUsuarioId(usuarioMsvc.getId());
			curso.removeCursoUsuario(cursoUsuario);
			cursoRepository.save(curso);
			return Optional.of(usuarioMsvc);
		}
		return Optional.empty();
	}

	@Transactional(readOnly = true)
	@Override
	public Optional<Curso> porIdConUsuarios(Long id) {
		Optional<Curso> o = cursoRepository.findById(id);
		if (o.isPresent()) {
			Curso curso = o.get();
			if (!curso.getCursoUsuarios().isEmpty()) {
				List<Long> ids = curso.getCursoUsuarios().stream().map(cursoUsuario -> cursoUsuario.getUsuarioId())
						.collect(Collectors.toList());

				List<Usuario> usuarios = client.obtenerAlumnosPorCurso(ids);
				curso.setUsuarios(usuarios);
			}
			return Optional.of(curso);
		}
		return Optional.empty();
	}

	@Transactional
	@Override
	public void eliminarCursoUsuarioPorId(Long id) {
		cursoRepository.eliminarCursoUsuarioPorId(id);
	}

}
