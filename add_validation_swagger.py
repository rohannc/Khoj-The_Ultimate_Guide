import re
import os

def update_dto(filepath):
    if not os.path.exists(filepath):
        print(f"Not found: {filepath}")
        return
    with open(filepath, 'r') as f:
        content = f.read()

    # Add imports
    imports = """
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
"""
    if "import jakarta.validation.constraints" not in content:
        content = re.sub(r'(import java\.util\.UUID;)', r'\1' + imports, content)
    else:
        return # Already processed

    # Add annotations for fields
    content = re.sub(r'(private UUID patientId;)', r'@NotNull(message = "Patient ID cannot be null")\n    @Schema(description = "ID of the patient", example = "123e4567-e89b-12d3-a456-426614174000")\n    \1', content)
    content = re.sub(r'(private UUID doctorId;)', r'@NotNull(message = "Doctor ID cannot be null")\n    @Schema(description = "ID of the doctor", example = "123e4567-e89b-12d3-a456-426614174000")\n    \1', content)
    
    # Generic String not blank
    content = re.sub(r'(\s+)(private String [a-zA-Z]+;)', r'\1@NotBlank(message = "Field cannot be blank")\n\1@Schema(description = "Details about the field")\1\2', content)

    with open(filepath, 'w') as f:
        f.write(content)
    print(f"Updated {filepath}")

def update_controller(filepath, tag_name, tag_desc):
    if not os.path.exists(filepath):
        print(f"Not found: {filepath}")
        return
    with open(filepath, 'r') as f:
        content = f.read()

    imports = """
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
"""
    if "import io.swagger.v3.oas.annotations" not in content:
        content = re.sub(r'(import org\.springframework\.web\.bind\.annotation\.\*;)', r'\1' + imports, content)

    if "@Tag(" not in content:
        content = re.sub(r'(@RestController)', f'@Tag(name = "{tag_name}", description = "{tag_desc}")\n\\1', content)

    # Add @Valid to @RequestBody
    content = re.sub(r'(@RequestBody\s+)([A-Za-z0-9_]+DTO)', r'@Valid \1\2', content)

    # Add @Operation to GetMapping
    content = re.sub(r'(@GetMapping\([^)]+\)\s+public ResponseEntity<List<[^>]+>>\s+[a-zA-Z]+\()', r'@Operation(summary = "Get multiple records", description = "Retrieve a list of records")\n    \1', content)
    
    # Add @Operation to PostMapping
    content = re.sub(r'(@PostMapping\([^)]+\)\s+public ResponseEntity<[^>]+>\s+[a-zA-Z]+\()', r'@Operation(summary = "Create a record", description = "Creates a new record")\n    \1', content)

    with open(filepath, 'w') as f:
        f.write(content)
    print(f"Updated {filepath}")

base_dir = r"d:\Desktop\Khoj-The_Ultimate_Guide\src\main\java\com\rohan\Khoj"

dtos = [
    (rf"{base_dir}\healthrecord\HealthRecordDTO.java", r"HealthRecordDTO.java"),
    (rf"{base_dir}\prescription\PrescriptionDTO.java", r"PrescriptionDTO.java"),
    (rf"{base_dir}\vital\VitalDTO.java", r"VitalDTO.java"),
    (rf"{base_dir}\notification\NotificationDTO.java", r"NotificationDTO.java")
]

controllers = [
    (rf"{base_dir}\healthrecord\HealthRecordController.java", "Health Records", "Manage Health Records"),
    (rf"{base_dir}\prescription\PrescriptionController.java", "Prescriptions", "Manage Prescriptions"),
    (rf"{base_dir}\vital\VitalController.java", "Vitals", "Manage Vitals"),
    (rf"{base_dir}\notification\NotificationController.java", "Notifications", "Manage Notifications")
]

for dto, name in dtos:
    update_dto(dto)

for ctrl, name, desc in controllers:
    update_controller(ctrl, name, desc)

