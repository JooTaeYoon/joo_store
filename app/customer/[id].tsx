import { FontAwesome6 } from '@expo/vector-icons';
import { useLocalSearchParams, useRouter } from 'expo-router';
import React, { useEffect, useState } from 'react';
import {
  ActivityIndicator,
  FlatList,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';

export default function CustomerClothesScreen() {
  const { id, name } = useLocalSearchParams(); // 이동 시 전달받은 손님 ID와 이름
  const [clothes, setClothes] = useState([]);
  const [loading, setLoading] = useState(true);
  const router = useRouter();

  useEffect(() => {
    fetchCustomerClothes();
  }, [id]);

  const fetchCustomerClothes = async () => {
    try {
      // 안드로이드 에뮬레이터 대응 IP 10.0.2.2
      const response = await fetch(
        `http://10.0.2.2:8080/api/store/customer/${id}/clothes`,
      );
      const data = await response.json();
      setClothes(data);
    } catch (error) {
      console.error('의류 목록 로드 실패:', error);
    } finally {
      setLoading(false);
    }
  };

  const renderClothesItem = ({ item }: { item: any }) => (
    <View style={styles.clothesCard}>
      <View style={styles.cardHeader}>
        <FontAwesome6 name="shirt" size={18} color="#2e7d32" />
        <Text style={styles.clothesType}>{item.clothesType}</Text>
        <View style={styles.statusBadge}>
          <Text style={styles.statusText}>{item.status || '접수완료'}</Text>
        </View>
      </View>

      <View style={styles.cardBody}>
        <Text style={styles.priceText}>
          💰 {item.price?.toLocaleString()}원
        </Text>
        {item.comment && (
          <Text style={styles.commentText}>💬 {item.comment}</Text>
        )}
      </View>
    </View>
  );

  return (
    <View style={styles.container}>
      {/* 헤더 영역 */}
      <View style={styles.topBar}>
        <TouchableOpacity onPress={() => router.back()}>
          <FontAwesome6 name="arrow-left" size={20} color="#333" />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>{name} 님의 세탁 이력</Text>
        <View style={{ width: 20 }} />
      </View>

      {loading ? (
        <View style={styles.center}>
          <ActivityIndicator size="large" color="#2e7d32" />
        </View>
      ) : (
        <FlatList
          data={clothes}
          keyExtractor={(item, index) => index.toString()}
          renderItem={renderClothesItem}
          ListEmptyComponent={
            <View style={styles.emptyContainer}>
              <FontAwesome6 name="box-open" size={50} color="#ccc" />
              <Text style={styles.emptyText}>맡기신 옷이 없습니다.</Text>
            </View>
          }
          contentContainerStyle={{ padding: 20 }}
        />
      )}
      <TouchableOpacity
        style={styles.fab}
        onPress={() =>
          router.push({
            pathname: '/clothes/create', // 새로 만들 옷 등록 페이지 주소
            params: { customerId: id, customerName: name }, // 현재 손님 정보를 넘김
          })
        }
      >
        <FontAwesome6 name="plus" size={24} color="#fff" />
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  topBar: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingTop: 60,
    paddingHorizontal: 20,
    paddingBottom: 20,
    backgroundColor: '#fff',
  },
  headerTitle: { fontSize: 18, fontWeight: 'bold', color: '#333' },
  clothesCard: {
    backgroundColor: '#fff',
    borderRadius: 12,
    padding: 15,
    marginBottom: 15,
    elevation: 3,
    shadowColor: '#000',
    shadowOpacity: 0.05,
    shadowRadius: 5,
  },
  cardHeader: { flexDirection: 'row', alignItems: 'center', marginBottom: 10 },
  clothesType: { fontSize: 17, fontWeight: 'bold', marginLeft: 10, flex: 1 },
  statusBadge: {
    backgroundColor: '#e8f5e9',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 5,
  },
  statusText: { color: '#2e7d32', fontSize: 12, fontWeight: 'bold' },
  cardBody: { borderTopWidth: 1, borderTopColor: '#f0f0f0', paddingTop: 10 },
  priceText: { fontSize: 15, color: '#333', fontWeight: '600' },
  commentText: { fontSize: 14, color: '#666', marginTop: 5 },
  center: { flex: 1, justifyContent: 'center', alignItems: 'center' },
  emptyContainer: { alignItems: 'center', marginTop: 100 },
  emptyText: { marginTop: 10, color: '#999', fontSize: 16 },
  fab: {
    position: 'absolute',
    right: 20,
    bottom: 30,
    backgroundColor: '#2e7d32', // 우리집 세탁소 메인 컬러
    width: 60,
    height: 60,
    borderRadius: 30,
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 5, // Android 그림자
    shadowColor: '#000', // iOS 그림자
    shadowOpacity: 0.3,
    shadowRadius: 5,
    shadowOffset: { width: 0, height: 2 },
  },
});
