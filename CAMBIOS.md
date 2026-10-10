# Iteración 1 – Qué modificamos

Partimos del sistema de Gestión de Reclamos municipal (Paradigma Orientado a Objetos)
y lo adaptamos a un **barrio privado**.

## Dominio

| Antes (municipio)                        | Ahora (barrio privado)                                  | Por qué |
|------------------------------------------|---------------------------------------------------------|---------|
| `Vecino.direccion: String`               | Nueva clase **`Lote`** (`numero`, `sector`) y `Vecino → 1 Lote` | Varios vecinos pueden vivir en el mismo lote y tienen que apuntar al mismo objeto; con texto, "Lote 47" y "lote 47" serían distintos. |
| `EmpleadoMunicipal` + `SectorEmpleado`   | **`Empleado`** + enum **`AreaEmpleado`** (SEGURIDAD, MANTENIMIENTO, JARDINERIA, ADMINISTRACION) | El personal del barrio no se comporta distinto según el área: el área es una etiqueta, no amerita subclases. |
| `Administrador`                          | **`Intendente`**                                        | Se confundía con el área ADMINISTRACION. El intendente es el rol que recibe y deriva los reclamos. |
| `TipoReclamo`: BACHE, SEMAFORO, etc.     | Tipos del barrio, **cada uno con su área** (`getArea()`) | Experto en Información: el tipo sabe a qué área va; un tipo nuevo se agrega en un solo lugar. |

Tipos: ACCESO_NO_AUTORIZADO, VELOCIDAD_EXCESIVA (SEGURIDAD) · PLOMERIA, ELECTRICIDAD (MANTENIMIENTO) ·
CORTE_DE_PASTO, PODA (JARDINERIA) · EXPENSAS, CONVIVENCIA, OTRO (ADMINISTRACION).

## Reglas nuevas

- **Un reclamo solo se puede asignar a un empleado del área de su tipo.** Lo controla
  `Reclamo.asignarEmpleado(...)` y, si no coincide, lanza `AsignacionInvalidaException`.
  La pantalla de asignación muestra solo empleados del área correcta.
- Un vecino no se puede registrar sin lote.
- Los lotes vienen precargados en `data/lotes.csv` (no hay alta de lotes desde el sistema).

## Renombres

| Antes                     | Ahora               |
|---------------------------|---------------------|
| `SistemaGestionReclamos`  | `ReclamoController` |
| `ServicioUsuarios`        | `UsuarioService`    |
| `ServicioReclamos`        | `ReclamoService`    |
| `username`                | `nombreUsuario`     |
| `getVecinoReporta()`      | `getVecino()`       |

## Persistencia

- Nuevo archivo `data/lotes.csv` (`numero;sector`).
- En `usuarios.csv`, la última columna del vecino pasa a ser el número de lote.
- `IRepositorio` suma `cargarLotes()` y `cargarUsuarios(...)` recibe los lotes para enlazar cada vecino con su `Lote`.

## Pendiente para próximas iteraciones

- State para `EstadoReclamo` (hoy cualquier estado puede pasar a cualquier otro).
- Reemplazar los `instanceof` del controlador por polimorfismo.
- Adapter para autenticación con un servicio externo.
- Asignación automática por área (Strategy para elegir al empleado).
