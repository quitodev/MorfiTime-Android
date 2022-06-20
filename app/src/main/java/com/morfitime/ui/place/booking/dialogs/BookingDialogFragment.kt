package com.morfitime.ui.place.booking.dialogs

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.morfitime.R
import com.morfitime.application.State
import com.morfitime.data.model.place.booking.BookingSchedule
import com.morfitime.databinding.FragmentBookingDialogBinding
import com.morfitime.presentation.place.booking.BookingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class BookingDialogFragment : DialogFragment() {

    // VIEW BINDING
    private var _binding: FragmentBookingDialogBinding? = null
    private val binding get() = _binding!!

    // VIEW MODEL
    private val viewModel by activityViewModels<BookingViewModel>()

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        _binding = FragmentBookingDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUserData()
        setEventsButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("SetTextI18n")
    private fun setUserData() {
        when (arguments?.getParcelable<BookingSchedule>("item")?.image) {
            "Vacío" -> binding.circleImage.setImageResource(R.drawable.ic_logo)
            else -> Glide.with(requireContext()).load(arguments?.getParcelable<BookingSchedule>("item")?.image).into(binding.circleImage)
        }
        binding.textSchedule.text = arguments?.getParcelable<BookingSchedule>("item")?.schedule
        binding.textPlace.text = arguments?.getParcelable<BookingSchedule>("item")?.place
        binding.textSector.text = arguments?.getParcelable<BookingSchedule>("item")?.name
        binding.textUser.text = arguments?.getParcelable<BookingSchedule>("item")?.user
        binding.textLimit.text = arguments?.getParcelable<BookingSchedule>("item")?.limit
        binding.textTolerance.text = arguments?.getParcelable<BookingSchedule>("item")?.tolerance
        val capacity = arguments?.getParcelable<BookingSchedule>("item")?.capacity!!.toInt()
        val people = arguments?.getParcelable<BookingSchedule>("item")?.people!!
        when {
            capacity < people -> binding.barPeople.max = capacity - 1
            capacity > people -> binding.barPeople.max = people - 1
            capacity == people -> binding.barPeople.max = people - 1
        }
        binding.barPeople.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                when (progress) {
                    0 -> binding.textPeople.text = "1 persona"
                    else -> binding.textPeople.text = "${progress + 1} personas"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private fun setEventsButtons() {
        binding.cardConfirm.setOnClickListener {
            val comments = when {
                binding.editComments.text.isEmpty() -> "No se agregaron"
                else -> "${binding.editComments.text}"
            }
            val map = mapOf(
                "comments" to comments,
                "date" to arguments?.getParcelable<BookingSchedule>("item")!!.date,
                "hour" to arguments?.getParcelable<BookingSchedule>("item")!!.hour,
                "limit" to arguments?.getParcelable<BookingSchedule>("item")!!.limit,
                "people" to binding.textPeople.text.toString().substringBefore(" "),
                "rejection" to "Vacío",
                "schedule" to arguments?.getParcelable<BookingSchedule>("item")!!.schedule,
                "sector" to arguments?.getParcelable<BookingSchedule>("item")!!.sector,
                "table" to "Sin asignar todavía",
                "user" to arguments?.getParcelable<BookingSchedule>("item")!!.user
            )
            updateBooking(map)
        }
        binding.cardClose.setOnClickListener { dismiss() }
    }

    private fun updateBooking(map: Map<String, String>) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(CREATED) {
                viewModel.updateBooking(map).collect { result ->
                    when (result) {
                        is State.Loading -> {
                            setTextsDisabled()
                            binding.layoutProgress.visibility = View.VISIBLE
                        }
                        is State.Success -> {
                            when {
                                result.data.isNotEmpty() -> {
                                    when (result.data) {
                                        "PENDIENTE" -> {
                                            binding.layoutProgress.visibility = View.GONE
                                            binding.layoutDone.visibility = View.VISIBLE
                                            binding.layoutDone.startAnimation(AnimationUtils.loadAnimation(context, R.anim.from_right))
                                            delay(2000)
                                            binding.layoutDone.visibility = View.GONE
                                            dismiss()
                                            findNavController().navigate(R.id.homeFragment)
                                        }
                                        "CON RESERVAS" -> {
                                            setTextsEnabled()
                                            binding.layoutProgress.visibility = View.GONE
                                            Toast.makeText(context, "Para poder reservar en este horario, cancelá las reservas anteriores!", Toast.LENGTH_SHORT).show()
                                        }
                                        "SIN CAPACIDAD" -> {
                                            setTextsEnabled()
                                            binding.layoutProgress.visibility = View.GONE
                                            Toast.makeText(context, "Se acaban de reservar los últimos lugares!", Toast.LENGTH_SHORT).show()
                                        }
                                        "LÍMITE ALCANZADO" -> {
                                            setTextsEnabled()
                                            binding.layoutProgress.visibility = View.GONE
                                            Toast.makeText(context, "Límite de reservas y/o cancelaciones alcanzado!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }
                        }
                        is State.Failure -> {
                            setTextsEnabled()
                            binding.layoutProgress.visibility = View.GONE
                            when {
                                "NullPointerException" in result.exception.toString() -> { }
                                "Missing or insufficient permissions" in result.exception.toString() -> { }
                                "Unable to resolve host" in result.exception.toString() -> {
                                    Toast.makeText(context, "No se pudo conectar. Revisá tu conexión!", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                    Toast.makeText(context, "Problemas de conexión! Si continúa, escribinos a soporte@morfitime.com.ar",
                                        Toast.LENGTH_LONG).show()
                                    Log.e("FIRESTORE BOOKING", result.exception.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setTextsEnabled() {
        binding.editComments.showSoftInputOnFocus = true
        binding.editComments.isEnabled = true
        binding.editComments.isFocusableInTouchMode = true
    }

    private fun setTextsDisabled() {
        binding.editComments.showSoftInputOnFocus = false
        binding.editComments.isEnabled = false
        binding.editComments.isFocusable = false
        binding.editComments.isFocusableInTouchMode = false
    }
}