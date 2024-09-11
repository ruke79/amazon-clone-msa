import { Form, Formik } from "formik";
import { useState } from "react";
import * as Yup from "yup";
import AdminInput from "../AdminInput";
import { TextField } from "@material-ui/core";
import { DesktopDatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFnsV3";
import api, { postRequest } from 'util/api';
import  toast from "react-hot-toast";



export default function Coupon({ setCoupons }) {
    const [name, setName] = useState("");
    const [discount, setDiscount] = useState(0);
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const [startDate, setStartDate] = useState(new Date());
    const [endDate, setEndDate] = useState(tomorrow);
  
    const handleStartDate = (newValue) => {
      setStartDate(newValue);
    };
    const handleEndDate = (newValue) => {
      setEndDate(newValue);
    };
    const validate = Yup.object({
      name: Yup.string()
        .required("Coupon name is required.")
        .min(2, "Coupon name must be bewteen 2 and 30 characters.")
        .max(30, "Coupon name must be bewteen 2 and 30 characters."),
      discount: Yup.number()
        .required("Discount is required.")
        .min(1, "Discount must be atleast 1%")
        .max(99, "Discount must be 99% or less"),
    });
    const submitHandler = async () => {
      try {
        if (startDate.toString() == endDate.toString()) {
          return toast.error("You can't pick the same Dates.");
        } else if (endDate.getTime() - startDate.getTime() < 0) {
          return toast.error("Start Date cannot be more than the end date.");
        }
        const { data } = await postRequest("/admin/coupon", {
          coupon: name,
          discount,
          startDate,
          endDate,
        });
        setCoupons(data.coupons);
        setName("");
        setDiscount(0);
        setStartDate(new Date());
        setEndDate(tomorrow);
        toast.success(data.message);
      } catch (error) {
        toast.error(error.response.data.message);
      }
    };
    return (
      <>
        <Formik
          enableReinitialize
          initialValues={{ name, discount }}
          validationSchema={validate}
          onSubmit={() => {
            submitHandler();
          }}
        >
          {(formik) => (
            <Form>
              <div className="flex p-2 border-b pb-1 font-semibold">Create a Coupon</div>
              <AdminInput
                type="text"
                label="Name"
                name="name"
                placholder="Coupon name"
                onChange={(e) => setName(e.target.value)}
              />
              <AdminInput
                type="number"
                label="Discount"
                name="discount"
                placholder="Discount"
                onChange={(e) => setDiscount(e.target.value)}
              />
              <div className="mt-4 flex items-center gap-8">
                <LocalizationProvider dateAdapter={AdapterDateFns}>
                  <DesktopDatePicker
                    label="Start Date"
                    inputFormat="MM/dd/yyyy"
                    value={startDate}
                    onChange={handleStartDate}
                    renderInput={(params) => <TextField {...params} />}
                    minDate={new Date()}
                  />
                  <DesktopDatePicker
                    label="End Date"
                    inputFormat="MM/dd/yyyy"
                    value={endDate}
                    onChange={handleEndDate}
                    renderInput={(params) => <TextField {...params} />}
                    minDate={tomorrow}
                  />
                </LocalizationProvider>
              </div>
              <div className="w-full flex flex-end">
                <button type="submit" className="min-w-32 py-2.5 px-3.5 font-semibold cursor-pointer text-black mt-2.5 transition-all">
                  <span>Add Coupon</span>
                </button>
              </div>
            </Form>
          )}
        </Formik>
      </>
    );
  }
  