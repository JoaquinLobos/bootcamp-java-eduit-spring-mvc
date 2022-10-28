package ar.com.educacionit.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ch.qos.logback.classic.Logger;


@Controller
@RequestMapping("/file")
public class LogController {

	Logger logger = (Logger) LoggerFactory.getLogger(LogController.class);
	@PostMapping("/upload")
		public String uploadFile(
				@RequestParam("file") MultipartFile file, 
				Model model)
				throws IOException {
		
			boolean error = false;

			// verifica si file == null
			if (file == null || file.isEmpty()) {
				logger.error("Null input");
				error = true;
				model.addAttribute("error", error);
				model.addAttribute("message", "Por favor seleccione un archivo");
				return "index";
			}
			
			// verifica el formato del archivo
			if(!this.extensionValidator(file)) {
				logger.error("Unacceptable file format");
				error = true;
				model.addAttribute("error", error);
				model.addAttribute("message", "Por favor seleccione un archivo con extension .txt o .csv");
				return "index";
			}
			
			// genera el path para el archivo y verifica si ya existe uno con el mismo nombre en la carpeta
			StringBuilder builder = new StringBuilder();
			builder.append("C:/desarrollo/upload_files");
			builder.append(File.separator);
			builder.append(file.getOriginalFilename());
			File checkFile = new File(builder.toString());
				if(checkFile.exists()) {
						logger.error("Already existing file");
						error = true;
						model.addAttribute("error", error);
						model.addAttribute("message", "Ya existe un archivo con ese nombre, por favor, cambie el nombre, o introduzca otro archivo");
						return "index";
				}
			
			
			// obtiene los fileBytes, crea el archivo y guarda el logback
			byte[] fileBytes = file.getBytes();
			Path path = Paths.get(builder.toString());
			Files.write(path, fileBytes, StandardOpenOption.CREATE);	
			logger.warn("File upload Name: " + file.getOriginalFilename()+"| Size: "+file.getSize()+" bytes");
			model.addAttribute("message", "Archivo cargado correctamente en la ruta: "+builder.toString());
			return "index";
		}
		
		// verifica la extension
		public boolean extensionValidator (MultipartFile file) {
			String name = file.getOriginalFilename();
			String [] arrayName = name.split("\\.");
			String ext = arrayName[arrayName.length-1]; 
			return ext.equalsIgnoreCase("csv")||ext.equalsIgnoreCase("txt");
		}
}
