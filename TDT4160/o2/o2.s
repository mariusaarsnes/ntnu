.thumb
.syntax unified

.include "gpio_constants.s"     // Register-adresser og konstanter for GPIO
.include "sys-tick_constants.s" // Register-adresser og konstanter for SysTick

.text
	.global Start


.global SysTick_Handler
.thumb_func
SysTick_Handler:
	LDR R7, [R0]
	CMP R7, R4
	BEQ handle_tenths
		// Hvis tenths ikke er 9 skal den økes
		ADD R7, R7, R3
		STR R7, [R0]
		b systick_handler_end // siden tenths ikke var 9 skal ikke sekunder/minutter økes

	handle_tenths:
		MOV R7, #0
		STR R7, [R0]

		//  Håndtering av led, ganske likt som øving 1
		MOV R7, #1
		LSL R7, #LED_PIN

		LDR R8, [R12,R6]
		AND R9, R8, R7
		CMP R9, R7
		BEQ led_is_on
			STR R7, [R12,R6]

			B led_done
		led_is_on:
			MOV R8, #0
			STR R8, [R12, R6]

		led_done:

		LDR R7, [R1]
		CMP R7, R5
		BEQ handle_secs
			// samme som med tenths.
			ADD R7, R7, R3
			STR R7, [R1]
			B systick_handler_end

		handle_secs:
			MOV R7, #0
			STR R7, [R1]

			LDR R7, [R2]
			CMP R7, R5
			BEQ handle_mins
				ADD R7, R7, R3
				STR R7, [R2]
				B systick_handler_end

			handle_mins:
			// Må sette alt til null
			MOV R7, #0
			STR R7, [R0]
			STR R7, [R1]
			STR R7, [R2]

	systick_handler_end:
	BX LR


.global GPIO_ODD_IRQHandler
.thumb_func
GPIO_ODD_IRQHandler:
	LDR R7,=SYSTICK_BASE
	MOV R8,#SYSTICK_CTRL
	LDR R9,[R7,R8]
	MOV R10,#1
	EOR R10, R10, R9
	STR R10,[R7, R8]

	LDR R7,=GPIO_BASE
	MOV R8,#GPIO_IFC
	MOV R9,#1
	LSL R9, R9, #9

	STR R9, [R7, R8]


	BX LR


Start:
# tenths = R0
# seconds = R1
# minutes = R2
# increment value = R3
# max tenths = R4
# max seconds, max minutes = R5
# LED0 = R6
# GPIO_BASE = R12
# ledig: R7, R8, R9, R10, R11


// Initiere klokka
LDR R0, =SYSTICK_BASE
MOV R1, #SYSTICK_CTRL
MOV R3, 0b110
STR R3, [R0,R1]

// Sette opp hvor ofte interrupts skal skje

MOV R1, #SYSTICK_LOAD
LDR R3, =FREQUENCY/10
STR R3, [R0,R1]

// Sette VAL til riktig verdi
MOV R1, #SYSTICK_VAL
STR R3, [R0,R1]


// fikser interrupt for knappen
MOV R0, #GPIO_EXTIPSELH
LDR R10, =GPIO_BASE

MOV R1, 0b1111
LSL R2, R1, #4
MVN R3, R2
LDR R4, [R10,R0]
AND R5, R3, R4
MOV R6, #PORT_B
LSL R7, R6, #4
ORR R8, R5, R7
STR R8, [R10,R0]

// EXTIFALL, bit 9 må settes til 1, pga det er porten til knappen

MOV R0,#1
MOV R1,#GPIO_EXTIFALL
ADD R2, R10, R1
LSL R4,R0,#9
LDR R5,[R2]
ORR R6,R5,R4
STR R6,[R2]

MOV R1,#GPIO_IFC
MOV R3,#1
LSL R3, R3, #9

STR R3, [R10, R1]

//ENBLE INTERRUPT
MOV R0,#1

LSL R1,R0,#9
MOV R2,#GPIO_IEN
ADD R3, R2, R10
LDR R4,[R3]
ORR R5,R4,R1
STR R5,[R3]


// Last inn riktige register for å kunne vise siffer på klokka, se i toppen av START for beskrivelse av register
LDR R0,=tenths
LDR R1,=seconds
LDR R2,=minutes
MOV R3,#1
MOV R4,#9
MOV R5,#59
LDR R12, =GPIO_BASE

MOV R7, #LED_PORT
MOV R8, #PORT_SIZE
MUL R6, R7, R8
ADD R6, R6, #GPIO_PORT_DOUT

loop:
B loop



NOP // Behold denne pÃ¥ bunnen av fila

