package org.bonomia.springcloud.msvc.cursos.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import org.bonomia.springcloud.msvc.cursos.models.Usuario;
import org.bonomia.springcloud.msvc.cursos.models.entity.Curso;
import org.bonomia.springcloud.msvc.cursos.services.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import feign.FeignException;
import feign.FeignException.FeignClientException;

@RestController
public class CursoController {

	@Autowired
	private CursoService service;

	@GetMapping
	public ResponseEntity<List<Curso>> listar() {
		return ResponseEntity.ok(service.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> detalle(@PathVariable Long id) {
		Optional<Curso> o = service.porIdConUsuarios(id);
		if (o.isPresent())
			return ResponseEntity.ok(o.get());

		return ResponseEntity.notFound().build();
	}

	@PostMapping
	public ResponseEntity<?> guardar(@Valid @RequestBody Curso curso, BindingResult result) {
		if (result.hasErrors()) {
			return validarCampos(result);
		}
		Curso cursoDB = service.guardar(curso);
		return ResponseEntity.status(HttpStatus.CREATED).body(cursoDB);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editar(@Valid @RequestBody Curso curso, @PathVariable Long id, BindingResult result) {
		if (result.hasErrors()) {
			return validarCampos(result);
		}
		Optional<Curso> o = service.porId(id);
		if (o.isPresent()) {
			Curso cursoDB = o.get();
			cursoDB.setNombre(curso.getNombre());
			return ResponseEntity.status(HttpStatus.CREATED).body(service.guardar(cursoDB));
		}

		return ResponseEntity.notFound().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> eliminar(@PathVariable Long id) {
		Optional<Curso> o = service.porId(id);
		if (o.isPresent()) {
			service.eliminar(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}

	@PutMapping("/asignar-usuario/{cursoId}")
	public ResponseEntity<?> asignarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
		Optional<Usuario> o;

		try {
			o = service.asignarUsuario(usuario, cursoId);
		} catch (FeignException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje",
					"No existe el usuario por el id o error en la comunicacion: " + e.getMessage()));
		}

		if (o.isPresent()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
		}
		return ResponseEntity.notFound().build();
	}
	
	@PostMapping("/crear-usuario/{cursoId}")
	public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
		Optional<Usuario> o;
		try {
			o = service.crearUsuario(usuario, cursoId);
		} catch (FeignException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje",
					"No se pudo crear el usuario o error en la comunicacion: " + e.getMessage()));
		}
		if (o.isPresent()) {
			return ResponseEntity.status(HttpStatus.CREATED).body(o.get());
		}
		return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/eliminar-usuario/{cursoId}")
	public ResponseEntity<?> eliminarUsuario(@RequestBody Usuario usuario, @PathVariable Long cursoId) {
		Optional<Usuario> o;
		try {
			o = service.eliminarUsuario(usuario, cursoId);
		} catch (FeignException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("mensaje",
					"No se pudo eliminar el usuario o error en la comunicacion: " + e.getMessage()));
		}
		if (o.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(o.get());
		}
		return ResponseEntity.notFound().build();
	}
	
	@DeleteMapping("/eliminar-curso-usuario/{id}")
	public ResponseEntity<?> eliminarCursoUsuarioPorId(@PathVariable Long id) {
		service.eliminarCursoUsuarioPorId(id);
		return ResponseEntity.noContent().build();
	}

	private ResponseEntity<Map<String, String>> validarCampos(BindingResult result) {
		Map<String, String> errores = new HashMap<>();
		result.getFieldErrors().forEach(err -> {
			errores.put(err.getField(), "El campo " + err.getField() + " " + err.getDefaultMessage());
		});
		return ResponseEntity.badRequest().body(errores);
	}

}
