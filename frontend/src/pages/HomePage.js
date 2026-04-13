import React, { useEffect, useMemo, useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css";

const API_COMPONENTS = "http://localhost:8081/api/components";
const API_CONFIG = "http://localhost:8081/api";

const emptyConfig = {
    cpu: null,
    gpu: null,
    motherboard: null,
    ram: null,
    psu: null,
    pcCase: null,
};

function money(v) {
    const n = Number(v || 0);
    return Number.isFinite(n) ? n.toLocaleString('ru-RU') + ' ₽' : "0 ₽";
}

function getItems(payload) {
    if (Array.isArray(payload)) return payload;
    if (payload?.items && Array.isArray(payload.items)) return payload.items;
    if (payload?.content && Array.isArray(payload.content)) return payload.content;
    if (payload?.data && Array.isArray(payload.data)) return payload.data;
    return [];
}

function getItemName(item, lists) {
    let type = null;

    for (const [key, list] of Object.entries(lists)) {
        if (list.includes(item)) {
            type = key;
            break;
        }
    }

    switch (type) {
        case "cpu":
            return `${item.name} ${item.socket}`;
        case "gpu":
            return `${item.producer} ${item.model}`;
        case "motherboard":
            return `${item.producer} ${item.socket}`;
        case "ram":
            return `${item.producer} ${item.ramType}`;
        case "psu":
            return `${item.producer} ${item.size} ${item.watt}W ${item.efficiencyRating}`;
        case "pcCase":
            return `${item.producer}`;
        default:
            return item.name || item.producer || item.title || `ID ${item.id}`;
    }
}

function labelOf(item, lists) {
    let itemName = getItemName(item, lists);
    return `${itemName} — ${money(item.price)}`;
}

export default function ConfiguratorPage() {
    const [lists, setLists] = useState({
        cpu: [],
        gpu: [],
        motherboard: [],
        ram: [],
        psu: [],
        pcCase: [],
    });

    const [selected, setSelected] = useState(emptyConfig);
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    const [message, setMessage] = useState("");
    const [isEditing, setIsEditing] = useState(false);
    const [editConfigId, setEditConfigId] = useState(null);

    // 🔥 ЛОГИКА РЕДАКТИРОВАНИЯ
    useEffect(() => {
        const urlParams = new URLSearchParams(window.location.search);
        const editId = urlParams.get('edit');
        const editConfig = sessionStorage.getItem('editConfig');

        if (editId || editConfig) {
            loadConfigForEdit(editId || editConfig);
        }
    }, []);

    const loadConfigForEdit = async (configId) => {
        try {
            setLoading(true);
            const config = await fetch(`${API_CONFIG}/configuration/id?id=${configId}`, {
                credentials: 'include'
            }).then(r => r.json());

            // Загружаем полные компоненты по ID из конфигурации
            const updatedSelected = { ...emptyConfig };

            if (config.cpuId) updatedSelected.cpu = lists.cpu.find(c => c.id == config.cpuId) || null;
            if (config.gpuId) updatedSelected.gpu = lists.gpu.find(g => g.id == config.gpuId) || null;
            if (config.motherboardId) updatedSelected.motherboard = lists.motherboard.find(m => m.id == config.motherboardId) || null;
            if (config.ramId) updatedSelected.ram = lists.ram.find(r => r.id == config.ramId) || null;
            if (config.psuId) updatedSelected.psu = lists.psu.find(p => p.id == config.psuId) || null;
            if (config.pcCaseId) updatedSelected.pcCase = lists.pcCase.find(c => c.id == config.pcCaseId) || null;

            setSelected(updatedSelected);
            setIsEditing(true);
            setEditConfigId(configId);
            setMessage(`Загружена конфигурация #${configId} для редактирования`);
            sessionStorage.removeItem('editConfig');
        } catch (e) {
            setMessage("Ошибка загрузки конфигурации для редактирования");
        }
    };

    // Загрузка компонентов
    useEffect(() => {
        (async () => {
            try {
                const [cpu, gpu, motherboard, ram, psu, pcCase] = await Promise.all([
                    fetch(`${API_COMPONENTS}/cpu`).then(r => r.json()),
                    fetch(`${API_COMPONENTS}/gpu`).then(r => r.json()),
                    fetch(`${API_COMPONENTS}/motherboard`).then(r => r.json()),
                    fetch(`${API_COMPONENTS}/ram`).then(r => r.json()),
                    fetch(`${API_COMPONENTS}/psu`).then(r => r.json()),
                    fetch(`${API_COMPONENTS}/pc_case`).then(r => r.json()),
                ]);

                setLists({
                    cpu: getItems(cpu),
                    gpu: getItems(gpu),
                    motherboard: getItems(motherboard),
                    ram: getItems(ram),
                    psu: getItems(psu),
                    pcCase: getItems(pcCase),
                });
            } catch (e) {
                setMessage("Не удалось загрузить компоненты");
            } finally {
                setLoading(false);
            }
        })();
    }, []);

    const total = useMemo(() => {
        return Object.values(selected).reduce((sum, item) => sum + Number(item?.price || 0), 0);
    }, [selected]);

    const selectedItems = useMemo(() => {
        return Object.entries(selected)
            .filter(([, item]) => item)
            .map(([key, item]) => ({ key, item }));
    }, [selected]);

    const handleChange = (key, id, listKey) => {
        const item = lists[listKey].find(x => String(x.id) === String(id)) || null;
        setSelected(prev => ({ ...prev, [key]: item }));
    };

    // 🔥 ИСПРАВЛЕННАЯ ЛОГИКА СОХРАНЕНИЯ
    const handleSave = async () => {
        setSaving(true);
        setMessage("");

        try {
            // Проверяем, что выбраны обязательные компоненты
            const required = ['cpu', 'motherboard', 'ram', 'psu'];
            const missing = required.filter(comp => !selected[comp]);

            if (missing.length > 0) {
                setMessage(`Выберите: ${missing.join(', ')}`);
                setSaving(false);
                return;
            }

            const payload = {
                cpuId: selected.cpu?.id,
                gpuId: selected.gpu?.id,
                motherboardId: selected.motherboard?.id,
                ramId: selected.ram?.id,
                psuId: selected.psu?.id,
                pcCaseId: selected.pcCase?.id,
                totalPrice: total,
                // Если редактируем - передаем ID
                ...(isEditing && editConfigId && { id: editConfigId })
            };

            const res = await fetch(`${API_CONFIG}/configuration`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify(payload),
            });

            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(`Ошибка сохранения: ${errorText}`);
            }

            const result = await res.json();
            setMessage(isEditing ? "Конфигурация обновлена!" : "Конфигурация сохранена!");
            setIsEditing(false);
            setEditConfigId(null);

        } catch (e) {
            console.error('Ошибка сохранения:', e);
            setMessage(`Ошибка: ${e.message}`);
        } finally {
            setSaving(false);
        }
    };

    const handleOrder = async () => {
        setMessage("");
        try {
            // Для заказа нужна сохраненная конфигурация с ID
            if (!isEditing && !editConfigId) {
                await handleSave();
                return;
            }

            const payload = {
                id: editConfigId || selected.id, // ID из редактируемой конфигурации
            };

            const res = await fetch(`${API_CONFIG}/configuration/toOrder`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                credentials: "include",
                body: JSON.stringify(payload),
                redirect: "follow",
            });

            if (res.redirected) {
                window.location.href = res.url;
                return;
            }
            throw new Error("Редирект не сработал");
        } catch (e) {
            setMessage("Не удалось отправить в заказ");
        }
    };

    if (loading) {
        return <div className="container py-5">Загрузка...</div>;
    }

    return (
        <div className="container py-4">
            <div className="row mb-4 align-items-center">
                <div className="col">
                    <h1 className="h3">
                        {isEditing ? (
                            <>
                                <i className="bi bi-pencil-square text-warning me-2"></i>
                                Редактирование конфигурации #{editConfigId}
                            </>
                        ) : (
                            <>
                                <i className="bi bi-gear-fill me-2 text-primary"></i>
                                Конфигуратор ПК
                            </>
                        )}
                    </h1>
                    <div className="text-muted">
                        {isEditing ? "Измените сборку и сохраните изменения" : "Собери компьютер из доступных компонентов"}
                    </div>
                </div>
            </div>

            <div className="row g-3">
                <div className="col-lg-8">
                    <div className="card mb-3">
                        <div className="card-body">
                            <label className="form-label">CPU <span className="text-danger">*</span></label>
                            <select className="form-select mb-3" onChange={(e) => handleChange("cpu", e.target.value, "cpu")} value={selected.cpu?.id || ""}>
                                <option value="">Не выбрано</option>
                                {lists.cpu.map(item => <option key={item.id} value={item.id}>{labelOf(item, lists)}</option>)}
                            </select>

                            <label className="form-label">GPU</label>
                            <select className="form-select mb-3" onChange={(e) => handleChange("gpu", e.target.value, "gpu")} value={selected.gpu?.id || ""}>
                                <option value="">Не выбрано</option>
                                {lists.gpu.map(item => <option key={item.id} value={item.id}>{labelOf(item, lists)}</option>)}
                            </select>

                            <label className="form-label">Материнская плата <span className="text-danger">*</span></label>
                            <select className="form-select mb-3" onChange={(e) => handleChange("motherboard", e.target.value, "motherboard")} value={selected.motherboard?.id || ""}>
                                <option value="">Не выбрано</option>
                                {lists.motherboard.map(item => <option key={item.id} value={item.id}>{labelOf(item, lists)}</option>)}
                            </select>

                            <label className="form-label">Оперативная память <span className="text-danger">*</span></label>
                            <select className="form-select mb-3" onChange={(e) => handleChange("ram", e.target.value, "ram")} value={selected.ram?.id || ""}>
                                <option value="">Не выбрано</option>
                                {lists.ram.map(item => <option key={item.id} value={item.id}>{labelOf(item, lists)}</option>)}
                            </select>

                            <label className="form-label">Блок питания <span className="text-danger">*</span></label>
                            <select className="form-select mb-3" onChange={(e) => handleChange("psu", e.target.value, "psu")} value={selected.psu?.id || ""}>
                                <option value="">Не выбрано</option>
                                {lists.psu.map(item => <option key={item.id} value={item.id}>{labelOf(item, lists)}</option>)}
                            </select>

                            <label className="form-label">Корпус</label>
                            <select className="form-select" onChange={(e) => handleChange("pcCase", e.target.value, "pcCase")} value={selected.pcCase?.id || ""}>
                                <option value="">Не выбрано</option>
                                {lists.pcCase.map(item => <option key={item.id} value={item.id}>{labelOf(item, lists)}</option>)}
                            </select>
                        </div>
                    </div>
                </div>

                <div className="col-lg-4">
                    <div className="card position-sticky" style={{ top: 20 }}>
                        <div className="card-body">
                            <h5 className="card-title">
                                Сборка {isEditing && "(редактирование)"}
                            </h5>

                            {selectedItems.length === 0 ? (
                                <div className="text-muted mb-3">Компоненты не выбраны</div>
                            ) : (
                                <div className="mb-3">
                                    {selectedItems.map(({ key, item }) => (
                                        <div key={key} className="d-flex justify-content-between mb-2 small">
                                            <span className="text-capitalize">{key}</span>
                                            <span className="fw-medium">{getItemName(item, lists)}</span>
                                        </div>
                                    ))}
                                </div>
                            )}

                            <hr />

                            <div className="d-flex justify-content-between mb-3">
                                <strong className="h4 mb-0">Итого:</strong>
                                <strong className="h4 mb-0 text-success">{money(total)}</strong>
                            </div>

                            {message && (
                                <div className={`alert alert-${message.includes('Ошибка') ? 'danger' : 'success'} py-2 mb-3 small`}>
                                    {message}
                                </div>
                            )}

                            <div className="d-grid gap-2">
                                <button
                                    className={`btn btn-${isEditing ? 'warning' : 'primary'}`}
                                    onClick={handleSave}
                                    disabled={saving}
                                >
                                    {saving ? (
                                        <>
                                            <span className="spinner-border spinner-border-sm me-2"></span>
                                            {isEditing ? 'Обновление...' : 'Сохранение...'}
                                        </>
                                    ) : (
                                        <>
                                            <i className={`bi bi-${isEditing ? 'pencil' : 'save'}-fill me-2`}></i>
                                            {isEditing ? 'Обновить конфигурацию' : 'Сохранить конфигурацию'}
                                        </>
                                    )}
                                </button>
                                <button
                                    className="btn btn-success"
                                    onClick={handleOrder}
                                    disabled={saving || (!isEditing && selectedItems.length === 0)}
                                >
                                    <i className="bi bi-cart-fill me-2"></i>
                                    В заказ
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}