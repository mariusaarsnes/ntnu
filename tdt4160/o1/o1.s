.thumb
.syntax unified

.include "gpio_constants.s"     // Register-adresser og konstanter for GPIO

.text
	.global Start
	
Start:

    // Skriv din kode her...
    // Henter ut GPIO_BASE til senere bruk
    LDR R0, =GPIO_BASE

    // Regner ut adressen for buttonPorten
    MOV R1, #BUTTON_PORT
    MOV R2, #PORT_SIZE
    MUL R1, R1, R2
   	LDR R2, =GPIO_PORT_DIN
    ADD R3, R1, R2

    // Regner ut adressen for ledPorten
    MOV R1, #LED_PORT
    MOV R2, #PORT_SIZE
    MUL R1, R1, R2
    LDR R2, =GPIO_PORT_DOUT
    ADD R1, R1, R2

    // Sjekker om knapp er trykket
sjekk_knapp:
	LDR R4, [R0,R3]
	MOV R5, #1
	LSL R5, R5, #BUTTON_PIN
	AND R6, R4, R5
	CMP R6, #0
    BNE ikke_trykket

	MOV R4, #1
    LSL R4, R4, #LED_PIN
    STR R4, [R0, R1]

    B sjekk_knapp

ikke_trykket:
	MOV R4, #0
	LSL R4, R4, #LED_PIN
	STR R4, [R0, R1]

	b sjekk_knapp



NOP // Behold denne på bunnen av fila

