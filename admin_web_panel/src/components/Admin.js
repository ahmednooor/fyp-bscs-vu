import React, { useState, useContext } from "react";
import { AdminContext, nullAdmin } from "../contexts/AdminContext";
import { updateAdmin } from "../services/updateAdmin";

const Admin = () => {
    const [admin, setAdmin] = useContext(AdminContext);
    const [newUsername, setNewUsername] = useState(admin.username);
    const [newPassword, setNewPassword] = useState(admin.password);
    const [newName, setNewName] = useState(admin.name);
    const [isPasswordVisible, setIsPasswordVisible] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [errStatus, setErrStatus] = useState("");

    const updateAdminInfo = (e) => {
        e.preventDefault();
        if (!isSaving) {
            setIsSaving(true);
            setErrStatus("");

            updateAdmin(
                admin.username,
                admin.password,
                newUsername,
                newPassword,
                newName,
                (data) => {
                    setErrStatus("");
                    setIsSaving(false);
                    setAdmin({ ...data, password: newPassword });
                },
                (error) => {
                    setErrStatus(error.toString());
                    setIsSaving(false);
                    setAdmin(nullAdmin);
                }
            );
        }
    };

    return (
        <div className="Admin">
            <div className="container">
                <div className="row">
                    <div className="twelve columns">
                        <h5>
                            Hello, <strong>{admin.name}</strong>
                        </h5>
                    </div>
                </div>
                <form onSubmit={updateAdminInfo}>
                    <div className="four columns">
                        <label>Admin Name</label>
                        <input
                            className="u-full-width"
                            type="text"
                            required
                            value={newName}
                            placeholder="e.g. John Doe"
                            onChange={(e) => {
                                setNewName(e.currentTarget.value);
                            }}
                            readOnly={isSaving ? true : false}
                        />
                    </div>
                    <div className="three columns">
                        <label>Admin Username</label>
                        <input
                            className="u-full-width"
                            type="text"
                            value={newUsername}
                            placeholder="e.g. p@55word"
                            onChange={(e) => {
                                setNewUsername(e.currentTarget.value);
                            }}
                            readOnly={isSaving ? true : false}
                        />
                    </div>
                    <div className="three columns">
                        <label>Admin Password</label>
                        <input
                            className="u-full-width"
                            type={isPasswordVisible ? "text" : "password"}
                            required
                            value={newPassword}
                            placeholder="e.g. p@55word"
                            onChange={(e) => {
                                setNewPassword(e.currentTarget.value);
                            }}
                            onFocus={(e) => {
                                setIsPasswordVisible(true);
                            }}
                            onBlur={(e) => {
                                setIsPasswordVisible(false);
                            }}
                            readOnly={isSaving ? true : false}
                        />
                    </div>
                    <div className="two columns">
                        <label>&emsp;</label>
                        <input
                            className={isSaving ? "u-full-width button" : "u-full-width button-primary"}
                            type="submit"
                            value={isSaving ? "Saving ..." : "Save"}
                        />
                    </div>
                </form>
                <div className="twelve columns">
                    {errStatus !== "" ? <p className="color-danger">{errStatus}</p> : null}
                    <hr />
                </div>
            </div>
        </div>
    );
};

export default Admin;
