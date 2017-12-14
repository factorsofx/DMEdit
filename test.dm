/datum/loot_crate_lock/decacode
	attempts_allowed = 3
	var/code = null
	var/lastattempt = null
	attempt_to_open(var/mob/living/opener)
		boutput(opener, "<span style=\"color:blue\">The crate is locked with a deca-code lock.</span>")
		var/input = input(usr, "Enter digit from 1 to 10.", "Deca-Code Lock") as null|num
		if (input < 1 || input > 10)
			boutput(opener, "You leave the crate alone.")
			return -1

		if (!inputter_check(opener))
			return

		src.lastattempt = input

		if (input == code)
			return 1
		else
			return 0

	read_device(var/mob/living/reader)
		boutput(reader, "<b>DECA-CODE LOCK REPORT:</b>")
		if (attempts_allowed == 1)
			boutput(reader, "<span style=\"color:red\">* Anti-tamper system will activate on next failed access attempt.</span>")
		else
			boutput(reader, "* Anti-tamper system will activate after [attempts_remaining] failed access attempts.")

		if (lastattempt == null)
			boutput(reader, "* No attempt has been made to open the crate thus far.")
			return

		if (code > src.lastattempt)
			boutput(reader, "* Last access attempt lower than expected code.")
		else
			boutput(reader, "* Last access attempt higher than expected code.")

	scramble_code()
		code = rand(1,10)
		attempts_remaining = attempts_allowed
		lastattempt = null
		revealed_code = initial(revealed_code)