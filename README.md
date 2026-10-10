# Gestión de Reclamos – Barrio Privado

Sistema de escritorio (Java + Swing) para que los vecinos de un barrio privado
carguen reclamos, el intendente los asigne al personal del área que corresponde
y los empleados actualicen su estado.

## Cómo correrlo

Desde la raíz del proyecto (tiene que poder leer la carpeta `data/`):

```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin vista.Main
```

En IntelliJ: marcar `src` como *Sources Root*, ejecutar `vista.Main` y usar
la raíz del proyecto como *Working directory*.

## Usuarios de prueba (contraseña `123`)

| Usuario          | Rol         | Detalle              |
|------------------|-------------|----------------------|
| `vecino`         | Vecino      | Lote 47              |
| `vecino2`        | Vecino      | Lote 12              |
| `vecino3`        | Vecino      | Lote 47              |
| `intendente`     | Intendente  | Asigna y rechaza     |
| `empleado`       | Empleado    | MANTENIMIENTO        |
| `guardia`        | Empleado    | SEGURIDAD            |
| `jardinero`      | Empleado    | JARDINERIA           |
| `administracion` | Empleado    | ADMINISTRACION       |

## Estructura

```
src/
  model/      clases del dominio (Usuario, Vecino, Empleado, Intendente, Lote, Reclamo, HistorialEstado)
  model/enums AreaEmpleado, TipoReclamo, EstadoReclamo
  negocio/    ReclamoController (punto de entrada), UsuarioService, ReclamoService
  repo/       IRepositorio (contrato) y GestorPersistencia (implementación con CSV)
  vista/      pantallas Swing
  excepts/    excepciones propias
data/         lotes.csv, usuarios.csv, reclamos.csv, historial.csv
```
