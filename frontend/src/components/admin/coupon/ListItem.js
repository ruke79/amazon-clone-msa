import { deleteRequest, putRequest } from "util/api";
import { useRef } from "react";
import { useState } from "react";
import { AiFillDelete, AiTwotoneEdit } from "react-icons/ai";
import  toast from "react-hot-toast";
import { TextField } from "@material-ui/core";
import { DesktopDatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFnsV3";


export default function ListItem({ coupon, setCoupons }) {
  const [open, setOpen] = useState(false);
  const [name, setName] = useState("");
  const [discount, setDiscount] = useState("");
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const [startDate, setStartDate] = useState(coupon.startDate);
  const [endDate, setEndDate] = useState(coupon.endDate);

  const handleStartDate = (newValue) => {
    setStartDate(newValue);
  };
  const handleEndDate = (newValue) => {
    setEndDate(newValue);
  };
  const input = useRef(null);
  const handleRemove = async (id) => {
    try {
      const { data } = await deleteRequest("/admin/coupon/delete", {
        data: { id },
      });
      setCoupons(data.coupons);
      toast.success(data.message);
    } catch (error) {
      toast.error(error.response.data.message);
    }
  };
  const handleUpdate = async (id) => {
    try {
      const { data } = await putRequest("/admin/coupon/update", {
        id,
        name: name || coupon.name,
        discount: discount || coupon.discount,
        startDate: startDate,
        endDate: endDate,
      });
      setCoupons(data.coupons);
      setOpen(false);
      toast.success(data.message);
    } catch (error) {
      toast.error(error.response.data.message);
    }
  };
  return (
    <li className="p-3 bg-blue-200 space-y-3 font-semibold text-white items-center justify-between md2:flex md2:gap-x-2.5">
      <input
        className={open ? "!bg-white !text-black" : ""}
        type="text"
        value={name ? name : coupon.name}
        onChange={(e) => setName(e.target.value)}
        disabled={!open}
        ref={input}
      />

      {open && (
        <div className="flex items-center gap-12 hover:bg-blue-500">
          <input
            className={open ? "!bg-white !text-black" : ""}
            type="text"
            value={discount ? discount : coupon.discount}
            onChange={(e) => setDiscount(e.target.value)}
            disabled={!open}
          />
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
          <button
            className={styles.btn}
            onClick={() => handleUpdate(coupon.id)}
          >
            Save
          </button>
          <button
            className="min-w-32 py-2.5 px-3.5 font-semibold cursor-pointer text-black mt-2.5 transition-all"
            onClick={() => {
              setOpen(false);
              setName("");
              setDiscount("");
              setStartDate(new Date());
              setEndDate(tomorrow);
            }}
          >
            Cancel
          </button>
        </div>
      )}
      <div className="">
        {!open && (
          <AiTwotoneEdit
            onClick={() => {
              setOpen((prev) => !prev);
              input.current.focus();
            }}
          />
        )}
        <AiFillDelete onClick={() => handleRemove(coupon.id)} />
      </div>
    </li>
  );
}
