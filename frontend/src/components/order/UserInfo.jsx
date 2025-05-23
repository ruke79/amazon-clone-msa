const UserInfo = ({ order, user }) => {
    return (
        <div className=" mb-2">
            <h3 className="text-2xl font-bold border-b pb-3 mb-3">
                {`Customer's Order`}
            </h3>

            <div className="flex items-center space-x-3">
                <img
                    src={user.image}
                    alt={user.name}
                    width={65}
                    height={65}
                    className="rounded-full"
                />
                <div className="flex flex-col text-slate-800">
                    <span className="font-semibold">{user.name}</span>
                    <span>{user.email}</span>
                </div>
            </div>
            <div className="mt-2 flex flex-col text-slate-800">
                <h4 className="text-xl font-bold border-b pb-2 mb-2">
                    Shipping Address
                </h4>
                <span>
                    {order.shippingAddress.firstname}
                    {order.shippingAddress.lastname}
                </span>
                <span>
                    {order.shippingAddress.city}/{order.shippingAddress.state}/
                    {order.shippingAddress.country}
                </span>
                <span>{order.shippingAddress.address1}</span>
                <span>{order.shippingAddress.address2}</span>
                <span>{order.shippingAddress.zipCode}</span>
                <span>{order.shippingAddress.phoneNumber}</span>
            </div>
            <div className="mt-2 flex flex-col text-slate-800">
                <h4 className="text-xl font-bold border-b pb-2 mb-2">
                    Billing Address
                </h4>
                <span>
                    {order.shippingAddress.firstname}{" "}
                    {order.shippingAddress.lastname}
                </span>
                <span>
                    {order.shippingAddress.city}/{order.shippingAddress.state}/
                    {order.shippingAddress.country}
                </span>
                <span>{order.shippingAddress.address1}</span>
                <span>{order.shippingAddress.address2}</span>
                <span>{order.shippingAddress.zipCode}</span>
                <span>{order.shippingAddress.phoneNumber}</span>
            </div>
        </div>
    );
};

export default UserInfo;
