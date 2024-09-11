import ListItem from "./ListItem";


export default function List({ coupons, setCoupons }) {
  return (
    <ul className="mt-4">
      {coupons.map((coupon) => (
        <ListItem coupon={coupon} key={coupon.id} setCoupons={setCoupons} />
      ))}
    </ul>
  );
}
