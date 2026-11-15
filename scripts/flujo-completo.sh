#!/usr/bin/env bash
# Recorre el ciclo completo de una orden contra la API en ejecucion.
# Uso:  ./scripts/flujo-completo.sh [http://localhost:8080]
set -euo pipefail

API="${1:-http://localhost:8080}/api/v1"
USUARIO="X-Usuario: recepcion01"

paso() { printf "\n\033[1m== %s\033[0m\n" "$1"; }
json() { python -c "import sys,json;d=json.load(sys.stdin);print(json.dumps(d,indent=2,ensure_ascii=False))"; }
campo() { python -c "import sys,json;print(json.load(sys.stdin).get('$1',''))"; }

paso "1. Estado del servicio"
curl -fsS "${1:-http://localhost:8080}/actuator/health" | json

paso "2. Crear la orden al recibir el vehiculo"
ORDEN=$(curl -fsS -X POST "$API/ordenes" -H "Content-Type: application/json" -H "$USUARIO" \
  -d '{"placa":"ABC123","kilometraje":48600,"motivo":"Revision de frenos"}')
echo "$ORDEN" | json
ID=$(echo "$ORDEN" | campo id)
echo "id de la orden: $ID"

paso "3. Registrar diagnostico manual (el escaner no esta conectado)"
curl -fsS -X POST "$API/ordenes/$ID/diagnosticos" -H "Content-Type: application/json" \
  -d '{"usarEscaner":true,"codigosFalla":["P0128","C1234"],"observaciones":"Pastillas al limite"}' | json

paso "4. Agregar servicio del catalogo"
curl -fsS -X POST "$API/ordenes/$ID/servicios?codigo=SRV-012" -H "$USUARIO" | json

paso "5. Emitir la cotizacion"
curl -fsS -X PATCH "$API/ordenes/$ID/estado" -H "Content-Type: application/json" -H "$USUARIO" \
  -d '{"estado":"COTIZADA"}' | json

paso "6. El cliente aprueba (canal WhatsApp)"
curl -fsS -X PATCH "$API/ordenes/$ID/estado" -H "Content-Type: application/json" -H "$USUARIO" \
  -d '{"estado":"APROBADA","canal":"WHATSAPP"}' | json

paso "7. Asignar mecanico disponible"
MEC=$(curl -fsS "$API/mecanicos/disponibles"   | python -c "import sys,json;m=json.load(sys.stdin);print(m[0]['id'] if m else '')")
if [ -z "$MEC" ]; then
  echo "   No hay mecanicos disponibles. Arranque con el perfil seed."
  exit 1
fi
curl -fsS -X PATCH "$API/ordenes/$ID/estado" -H "Content-Type: application/json" -H "$USUARIO"   -d "{\"estado\":\"EN_REPARACION\",\"idMecanico\":\"$MEC\"}" | json

paso "8. Intentar consumir un repuesto sin stock suficiente -> debe dar 409"
set +e
curl -s -o /tmp/err.json -w "   HTTP %{http_code}\n" -X POST "$API/ordenes/$ID/repuestos" \
  -H "Content-Type: application/json" -d '{"codigo":"FILT-009","cantidad":99}'
cat /tmp/err.json | json
set -e

paso "9. Transicion ilegal -> debe dar 409 enumerando las validas"
set +e
curl -s -o /tmp/err2.json -w "   HTTP %{http_code}\n" -X PATCH "$API/ordenes/$ID/estado" \
  -H "Content-Type: application/json" -d '{"estado":"ENTREGADA"}'
cat /tmp/err2.json | json
set -e

paso "10. Historial de estados"
curl -fsS "$API/ordenes/$ID/historial" | json

printf "\n\033[1mFlujo recorrido.\033[0m Revise arriba que los pasos 8 y 9 devolvieron 409.\n"
